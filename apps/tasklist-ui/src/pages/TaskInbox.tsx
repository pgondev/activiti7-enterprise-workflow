import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Search, Filter, Clock, User, CheckCircle, AlertCircle, ChevronRight } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

interface Task {
    id: string
    name: string
    processName: string
    assignee: string | null
    priority: 'HIGH' | 'MEDIUM' | 'LOW'
    dueDate: string | null
    createdAt: string
    status: 'CREATED' | 'ASSIGNED' | 'COMPLETED'
}

const mockTasks: Task[] = [
    { id: '1', name: 'Review Loan Application', processName: 'Loan Approval', assignee: null, priority: 'HIGH', dueDate: '2024-01-20', createdAt: '2024-01-15T10:00:00', status: 'CREATED' },
    { id: '2', name: 'Approve Expense Report', processName: 'Expense Approval', assignee: 'john.doe', priority: 'MEDIUM', dueDate: '2024-01-18', createdAt: '2024-01-14T14:30:00', status: 'ASSIGNED' },
    { id: '3', name: 'Verify Customer Documents', processName: 'Customer Onboarding', assignee: null, priority: 'HIGH', dueDate: null, createdAt: '2024-01-15T09:00:00', status: 'CREATED' },
    { id: '4', name: 'Schedule Interview', processName: 'Employee Onboarding', assignee: 'jane.smith', priority: 'LOW', dueDate: '2024-01-25', createdAt: '2024-01-13T11:00:00', status: 'ASSIGNED' },
    { id: '5', name: 'Process Refund Request', processName: 'Customer Support', assignee: null, priority: 'MEDIUM', dueDate: '2024-01-19', createdAt: '2024-01-15T08:00:00', status: 'CREATED' },
]

const priorityColors = {
    HIGH: 'bg-red-500/20 text-red-400 border border-red-500/30',
    MEDIUM: 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30',
    LOW: 'bg-green-500/20 text-green-400 border border-green-500/30',
}

export default function TaskInbox() {
    const [search, setSearch] = useState('')
    const [priorityFilter, setPriorityFilter] = useState<string>('all')

    const filteredTasks = mockTasks.filter((task) => {
        const matchesSearch = task.name.toLowerCase().includes(search.toLowerCase()) ||
            task.processName.toLowerCase().includes(search.toLowerCase())
        const matchesPriority = priorityFilter === 'all' || task.priority === priorityFilter
        return matchesSearch && matchesPriority
    })

    const unassignedTasks = filteredTasks.filter(t => !t.assignee)

    return (
        <div className="max-w-6xl mx-auto animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Task Inbox</h1>
                    <p className="text-slate-400">
                        {unassignedTasks.length} tasks available to claim
                    </p>
                </div>
            </div>

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
                    {(['all', 'HIGH', 'MEDIUM', 'LOW'] as const).map((p) => (
                        <button
                            key={p}
                            onClick={() => setPriorityFilter(p)}
                            className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${priorityFilter === p
                                    ? 'bg-blue-600 text-white'
                                    : 'text-slate-400 hover:text-white'
                                }`}
                        >
                            {p === 'all' ? 'All' : p}
                        </button>
                    ))}
                </div>
            </div>

            {/* Task List */}
            <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
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
                                        <span className={`px-2 py-0.5 text-xs rounded-full ${priorityColors[task.priority]}`}>
                                            {task.priority}
                                        </span>
                                    </div>

                                    <div className="flex items-center gap-4 text-sm text-slate-400">
                                        <span className="flex items-center gap-1">
                                            <CheckCircle size={14} />
                                            {task.processName}
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
                                            {formatDistanceToNow(new Date(task.createdAt), { addSuffix: true })}
                                        </span>

                                        {task.dueDate && (
                                            <span className={`flex items-center gap-1 ${new Date(task.dueDate) < new Date() ? 'text-red-400' : ''
                                                }`}>
                                                Due: {task.dueDate}
                                            </span>
                                        )}
                                    </div>
                                </div>

                                <ChevronRight size={20} className="text-slate-500 group-hover:text-white transition-colors" />
                            </div>
                        </Link>
                    ))}
                </div>
            </div>
        </div>
    )
}
