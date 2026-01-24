import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Search, Filter, Clock, User, CheckCircle, AlertCircle, ChevronRight, RefreshCw } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

interface Task {
    id: string
    name: string
    processName?: string
    processDefinitionId: string
    assignee: string | null
    createTime: string
}

// Simple API client since we don't have the full client library yet
const fetchTasks = async (assignee?: string) => {
    let url = '/api/v1/tasks?page=0&size=50'
    if (assignee) {
        url += `&assignee=${assignee}`
    }
    const res = await fetch(url)
    if (!res.ok) throw new Error('Failed to fetch tasks')
    return res.json()
}

export default function TaskInbox() {
    const [tasks, setTasks] = useState<Task[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [search, setSearch] = useState('')
    const [filter, setFilter] = useState<'all' | 'my' | 'unassigned'>('all')

    useEffect(() => {
        loadTasks()
    }, [filter])

    const loadTasks = async () => {
        setLoading(true)
        setError(null)
        try {
            // In a real app, currentUserId would come from auth context
            const currentUserId = 'john.doe'

            let data = []
            if (filter === 'my') {
                data = await fetchTasks(currentUserId)
            } else {
                // Determine filter logic - for now just fetch all and filter client side or use specialized endpoints
                // For demo simplicity, we'll fetch all and filter in memory if needed or rely on backend
                const res = await fetch(`/api/v1/tasks${filter === 'unassigned' ? '/claimable' : ''}?page=0&size=50`)
                if (res.ok) {
                    const page = await res.json()
                    data = page.content || page
                }
            }
            setTasks(data)
        } catch (err) {
            console.error(err)
            setError('Failed to load tasks. Verify backend is running.')
        } finally {
            setLoading(false)
        }
    }

    const filteredTasks = tasks.filter((task) => {
        const matchesSearch = task.name.toLowerCase().includes(search.toLowerCase())
        return matchesSearch
    })

    return (
        <div className="max-w-6xl mx-auto animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Task Inbox</h1>
                    <p className="text-slate-400">
                        Manage and complete your assigned tasks
                    </p>
                </div>
                <button
                    onClick={loadTasks}
                    className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg transition-colors"
                >
                    <RefreshCw size={20} className={loading ? "animate-spin" : ""} />
                </button>
            </div>

            {/* Error Banner */}
            {error && (
                <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-400 flex items-center gap-2">
                    <AlertCircle size={20} />
                    {error}
                </div>
            )}

            {/* Filters */}
            <div className="flex gap-4 mb-6">
                <div className="flex-1 relative">
                    <Search size={20} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search tasks..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-blue-500"
                    />
                </div>

                <div className="flex items-center gap-2 bg-slate-800 rounded-lg p-1 border border-slate-700">
                    <Filter size={18} className="text-slate-400 ml-2" />
                    <button
                        onClick={() => setFilter('all')}
                        className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${filter === 'all' ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-white'}`}
                    >
                        All
                    </button>
                    <button
                        onClick={() => setFilter('my')}
                        className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${filter === 'my' ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-white'}`}
                    >
                        My Tasks
                    </button>
                    <button
                        onClick={() => setFilter('unassigned')}
                        className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${filter === 'unassigned' ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-white'}`}
                    >
                        Unassigned
                    </button>
                </div>
            </div>

            {/* Task List */}
            <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
                {loading && (
                    <div className="p-8 text-center text-slate-400">
                        Loading tasks...
                    </div>
                )}

                {!loading && filteredTasks.length === 0 && (
                    <div className="p-8 text-center text-slate-400">
                        No tasks found
                    </div>
                )}

                {!loading && filteredTasks.length > 0 && (
                    <div className="divide-y divide-slate-700">
                        {filteredTasks.map((task) => (
                            <Link
                                key={task.id}
                                to={`/tasks/${task.id}`}
                                className="block p-4 hover:bg-slate-700/50 transition-colors group"
                            >
                                <div className="flex items-center justify-between">
                                    <div className="flex-1">
                                        <div className="flex items-center gap-3 mb-2">
                                            <h3 className="font-semibold text-white group-hover:text-blue-400 transition-colors">
                                                {task.name}
                                            </h3>
                                        </div>

                                        <div className="flex items-center gap-4 text-sm text-slate-400">
                                            <span className="flex items-center gap-1">
                                                <CheckCircle size={14} />
                                                ID: {task.id}
                                            </span>

                                            {task.assignee ? (
                                                <span className="flex items-center gap-1 text-purple-400">
                                                    <User size={14} />
                                                    {task.assignee}
                                                </span>
                                            ) : (
                                                <span className="flex items-center gap-1 text-yellow-400">
                                                    <AlertCircle size={14} />
                                                    Unassigned
                                                </span>
                                            )}

                                            <span className="flex items-center gap-1">
                                                <Clock size={14} />
                                                {task.createTime ? formatDistanceToNow(new Date(task.createTime), { addSuffix: true }) : 'Just now'}
                                            </span>
                                        </div>
                                    </div>

                                    <ChevronRight size={20} className="text-slate-500 group-hover:text-white transition-colors" />
                                </div>
                            </Link>
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}
