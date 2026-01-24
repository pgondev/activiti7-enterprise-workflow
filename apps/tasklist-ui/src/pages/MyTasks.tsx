import { Link } from 'react-router-dom'
import { Clock, CheckCircle, ChevronRight } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

interface Task {
    id: string
    name: string
    processName: string
    priority: 'HIGH' | 'MEDIUM' | 'LOW'
    dueDate: string | null
    createdAt: string
    claimedAt: string
}

const myTasks: Task[] = [
    { id: '2', name: 'Approve Expense Report', processName: 'Expense Approval', priority: 'MEDIUM', dueDate: '2024-01-18', createdAt: '2024-01-14T14:30:00', claimedAt: '2024-01-15T09:00:00' },
    { id: '4', name: 'Schedule Interview', processName: 'Employee Onboarding', priority: 'LOW', dueDate: '2024-01-25', createdAt: '2024-01-13T11:00:00', claimedAt: '2024-01-14T10:00:00' },
]

const priorityColors = {
    HIGH: 'bg-red-500/20 text-red-400 border border-red-500/30',
    MEDIUM: 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30',
    LOW: 'bg-green-500/20 text-green-400 border border-green-500/30',
}

export default function MyTasks() {
    return (
        <div className="max-w-6xl mx-auto animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">My Tasks</h1>
                    <p className="text-slate-400">
                        {myTasks.length} tasks assigned to you
                    </p>
                </div>
            </div>

            {/* Task Grid */}
            <div className="grid gap-4">
                {myTasks.map((task) => (
                    <Link
                        key={task.id}
                        to={`/tasks/${task.id}`}
                        className="bg-slate-800 rounded-xl border border-slate-700 p-5 hover:border-blue-500/50 transition-all group"
                    >
                        <div className="flex items-center justify-between">
                            <div className="flex-1">
                                <div className="flex items-center gap-3 mb-3">
                                    <h3 className="text-lg font-semibold text-white group-hover:text-blue-400 transition-colors">
                                        {task.name}
                                    </h3>
                                    <span className={`px-2 py-0.5 text-xs rounded-full ${priorityColors[task.priority]}`}>
                                        {task.priority}
                                    </span>
                                </div>

                                <div className="flex items-center gap-6 text-sm text-slate-400">
                                    <span className="flex items-center gap-2">
                                        <CheckCircle size={16} />
                                        {task.processName}
                                    </span>

                                    <span className="flex items-center gap-2">
                                        <Clock size={16} />
                                        Claimed {formatDistanceToNow(new Date(task.claimedAt), { addSuffix: true })}
                                    </span>

                                    {task.dueDate && (
                                        <span className={`${new Date(task.dueDate) < new Date() ? 'text-red-400' : 'text-slate-400'}`}>
                                            Due: {task.dueDate}
                                        </span>
                                    )}
                                </div>
                            </div>

                            <div className="flex items-center gap-3">
                                <button
                                    onClick={(e) => { e.preventDefault(); }}
                                    className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
                                >
                                    Complete
                                </button>
                                <ChevronRight size={20} className="text-slate-500 group-hover:text-white transition-colors" />
                            </div>
                        </div>
                    </Link>
                ))}

                {myTasks.length === 0 && (
                    <div className="text-center py-16 text-slate-400">
                        <CheckCircle size={48} className="mx-auto mb-4 opacity-50" />
                        <p className="text-lg">No tasks assigned to you</p>
                        <p className="text-sm mt-1">Claim tasks from the inbox to get started</p>
                    </div>
                )}
            </div>
        </div>
    )
}
