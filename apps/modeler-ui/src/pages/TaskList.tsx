import { useState, useEffect } from 'react'
import { CheckSquare, User, Clock, AlertCircle, RefreshCw, CheckCircle } from 'lucide-react'
import { taskApi } from '../api/client'
import { format } from 'date-fns'

interface Task {
    id: string
    name: string
    assignee: string | null
    createTime: string
    processInstanceId: string
    processDefinitionId: string
    status: 'ASSIGNED' | 'UNASSIGNED' | 'COMPLETED'
}

export default function TaskList() {
    const [tasks, setTasks] = useState<Task[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        fetchTasks()
    }, [])

    const fetchTasks = async () => {
        setLoading(true)
        try {
            const res = await taskApi.getTasks({ page: 0, size: 50 })
            const data = res.data.content || res.data
            setTasks(data.map((t: any) => ({
                ...t,
                status: t.assignee ? 'ASSIGNED' : 'UNASSIGNED'
            })))
            setError(null)
        } catch (err) {
            console.error(err)
            setError('Failed to fetch tasks')
        } finally {
            setLoading(false)
        }
    }

    const handleClaim = async (taskId: string) => {
        try {
            await taskApi.claim(taskId)
            fetchTasks()
        } catch (err) {
            alert('Failed to claim task')
        }
    }

    const handleComplete = async (taskId: string) => {
        try {
            await taskApi.complete(taskId)
            fetchTasks()
        } catch (err) {
            alert('Failed to complete task')
        }
    }

    return (
        <div className="p-8 animate-fade-in">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">My Tasks</h1>
                    <p className="text-slate-400">Manage and complete your assigned tasks</p>
                </div>
                <button
                    onClick={fetchTasks}
                    className="flex items-center gap-2 px-4 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors"
                >
                    <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
                    Refresh
                </button>
            </div>

            {error && (
                <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-400 flex items-center gap-2">
                    <AlertCircle size={20} />
                    {error}
                </div>
            )}

            <div className="grid grid-cols-1 gap-4">
                {tasks.map((task) => (
                    <div key={task.id} className="bg-slate-800 rounded-xl border border-slate-700 p-6 hover:border-blue-500/30 transition-all flex items-center justify-between">
                        <div className="flex items-start gap-4">
                            <div className={`p-3 rounded-lg ${task.assignee ? 'bg-blue-600/20 text-blue-400' : 'bg-slate-700 text-slate-400'}`}>
                                <CheckSquare size={24} />
                            </div>
                            <div>
                                <h3 className="text-lg font-semibold text-white mb-1">{task.name}</h3>
                                <div className="flex items-center gap-4 text-sm text-slate-400">
                                    <span className="flex items-center gap-1">
                                        <Clock size={14} />
                                        {format(new Date(task.createTime), 'MMM d, HH:mm')}
                                    </span>
                                    <span className="flex items-center gap-1">
                                        <User size={14} />
                                        {task.assignee || 'Unassigned'}
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div className="flex gap-2">
                            {!task.assignee ? (
                                <button
                                    onClick={() => handleClaim(task.id)}
                                    className="px-4 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg font-medium transition-colors"
                                >
                                    Claim
                                </button>
                            ) : (
                                <button
                                    onClick={() => handleComplete(task.id)}
                                    className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium transition-colors shadow-lg shadow-green-600/20"
                                >
                                    <CheckCircle size={18} />
                                    Complete
                                </button>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            {tasks.length === 0 && !loading && (
                <div className="text-center py-12 text-slate-500 bg-slate-800/50 rounded-xl border border-slate-700 border-dashed">
                    <CheckSquare size={48} className="mx-auto mb-4 opacity-50" />
                    <p>No pending tasks found</p>
                </div>
            )}
        </div>
    )
}
