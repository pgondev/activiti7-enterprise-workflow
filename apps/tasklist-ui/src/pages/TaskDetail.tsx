import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, User, Clock, CheckCircle, MessageSquare, Paperclip } from 'lucide-react'
import { formatDistanceToNow } from 'date-fns'

const taskData = {
    id: '1',
    name: 'Review Loan Application',
    processName: 'Loan Approval',
    processInstanceId: 'proc-12345',
    assignee: null,
    priority: 'HIGH' as const,
    dueDate: '2024-01-20',
    createdAt: '2024-01-15T10:00:00',
    description: 'Review the loan application and verify all submitted documents. Check credit score, income verification, and employment history.',
    variables: {
        applicantName: 'John Smith',
        loanAmount: 50000,
        loanType: 'Personal Loan',
        creditScore: 720,
    },
    comments: [
        { id: '1', author: 'system', message: 'Task created', timestamp: '2024-01-15T10:00:00' },
        { id: '2', author: 'jane.doe', message: 'Please prioritize this application', timestamp: '2024-01-15T11:30:00' },
    ],
}

const priorityColors = {
    HIGH: 'bg-red-500/20 text-red-400 border border-red-500/30',
    MEDIUM: 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30',
    LOW: 'bg-green-500/20 text-green-400 border border-green-500/30',
}

export default function TaskDetail() {
    const { taskId } = useParams()
    const navigate = useNavigate()

    const handleClaim = () => {
        console.log('Claiming task:', taskId)
        // TODO: Call API
    }

    const handleComplete = () => {
        console.log('Completing task:', taskId)
        // TODO: Call API
    }

    return (
        <div className="max-w-4xl mx-auto animate-fade-in">
            {/* Back Button */}
            <button
                onClick={() => navigate(-1)}
                className="flex items-center gap-2 text-slate-400 hover:text-white mb-6 transition-colors"
            >
                <ArrowLeft size={20} />
                Back to inbox
            </button>

            {/* Task Header */}
            <div className="bg-slate-800 rounded-xl border border-slate-700 p-6 mb-6">
                <div className="flex items-start justify-between mb-4">
                    <div>
                        <div className="flex items-center gap-3 mb-2">
                            <h1 className="text-2xl font-bold text-white">{taskData.name}</h1>
                            <span className={`px-2 py-0.5 text-xs rounded-full ${priorityColors[taskData.priority]}`}>
                                {taskData.priority}
                            </span>
                        </div>
                        <p className="text-slate-400">{taskData.processName}</p>
                    </div>

                    <div className="flex gap-3">
                        {!taskData.assignee ? (
                            <button
                                onClick={handleClaim}
                                className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
                            >
                                Claim Task
                            </button>
                        ) : (
                            <button
                                onClick={handleComplete}
                                className="px-5 py-2.5 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg transition-colors"
                            >
                                Complete Task
                            </button>
                        )}
                    </div>
                </div>

                <div className="flex items-center gap-6 text-sm text-slate-400">
                    {taskData.assignee ? (
                        <span className="flex items-center gap-2 text-purple-400">
                            <User size={16} />
                            Assigned to {taskData.assignee}
                        </span>
                    ) : (
                        <span className="flex items-center gap-2 text-yellow-400">
                            <User size={16} />
                            Unassigned
                        </span>
                    )}

                    <span className="flex items-center gap-2">
                        <Clock size={16} />
                        Created {formatDistanceToNow(new Date(taskData.createdAt), { addSuffix: true })}
                    </span>

                    {taskData.dueDate && (
                        <span className={`flex items-center gap-2 ${new Date(taskData.dueDate) < new Date() ? 'text-red-400' : ''
                            }`}>
                            <CheckCircle size={16} />
                            Due: {taskData.dueDate}
                        </span>
                    )}
                </div>
            </div>

            <div className="grid grid-cols-3 gap-6">
                {/* Main Content */}
                <div className="col-span-2 space-y-6">
                    {/* Description */}
                    <div className="bg-slate-800 rounded-xl border border-slate-700 p-6">
                        <h2 className="text-lg font-semibold text-white mb-4">Description</h2>
                        <p className="text-slate-300 leading-relaxed">{taskData.description}</p>
                    </div>

                    {/* Task Form (placeholder) */}
                    <div className="bg-slate-800 rounded-xl border border-slate-700 p-6">
                        <h2 className="text-lg font-semibold text-white mb-4">Task Form</h2>
                        <div className="bg-slate-700/50 rounded-lg p-8 text-center text-slate-400">
                            <p>Form.io form would render here</p>
                            <p className="text-sm mt-2">Based on the task's form key</p>
                        </div>
                    </div>

                    {/* Comments */}
                    <div className="bg-slate-800 rounded-xl border border-slate-700 p-6">
                        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                            <MessageSquare size={20} />
                            Comments
                        </h2>
                        <div className="space-y-4">
                            {taskData.comments.map((comment) => (
                                <div key={comment.id} className="flex gap-3">
                                    <div className="w-8 h-8 bg-slate-600 rounded-full flex items-center justify-center">
                                        <User size={14} className="text-slate-300" />
                                    </div>
                                    <div className="flex-1">
                                        <div className="flex items-center gap-2 mb-1">
                                            <span className="font-medium text-white">{comment.author}</span>
                                            <span className="text-xs text-slate-500">
                                                {formatDistanceToNow(new Date(comment.timestamp), { addSuffix: true })}
                                            </span>
                                        </div>
                                        <p className="text-slate-300 text-sm">{comment.message}</p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <div className="mt-4 pt-4 border-t border-slate-700">
                            <input
                                type="text"
                                placeholder="Add a comment..."
                                className="w-full px-4 py-2 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-blue-500"
                            />
                        </div>
                    </div>
                </div>

                {/* Sidebar */}
                <div className="space-y-6">
                    {/* Variables */}
                    <div className="bg-slate-800 rounded-xl border border-slate-700 p-6">
                        <h2 className="text-lg font-semibold text-white mb-4">Process Variables</h2>
                        <dl className="space-y-3">
                            {Object.entries(taskData.variables).map(([key, value]) => (
                                <div key={key}>
                                    <dt className="text-xs text-slate-500 uppercase">{key}</dt>
                                    <dd className="text-white font-medium">{String(value)}</dd>
                                </div>
                            ))}
                        </dl>
                    </div>

                    {/* Attachments */}
                    <div className="bg-slate-800 rounded-xl border border-slate-700 p-6">
                        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                            <Paperclip size={20} />
                            Attachments
                        </h2>
                        <div className="text-center py-6 text-slate-400">
                            <Paperclip size={32} className="mx-auto mb-2 opacity-50" />
                            <p className="text-sm">No attachments</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
