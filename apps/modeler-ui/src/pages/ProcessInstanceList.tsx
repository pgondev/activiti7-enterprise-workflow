import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Play, Eye, AlertCircle, RefreshCw, FileText } from 'lucide-react'
import { instanceApi } from '../api/client'
import { format } from 'date-fns'

interface ProcessInstance {
    id: string
    name: string
    processDefinitionId: string
    processDefinitionKey: string
    startTime: string
    endTime: string | null
    startUserId: string | null
    status: 'RUNNING' | 'COMPLETED' | 'SUSPENDED'
}

export default function ProcessInstanceList() {
    const [instances, setInstances] = useState<ProcessInstance[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        fetchInstances()
    }, [])

    const fetchInstances = async () => {
        setLoading(true)
        try {
            const res = await instanceApi.getInstances({ page: 0, size: 50 })
            // Adapt backend response to frontend model
            // Backend might return PagedResources
            const data = res.data.content || res.data // handle both PagedModel or List
            setInstances(data.map((i: any) => ({
                ...i,
                status: i.endTime ? 'COMPLETED' : (i.suspended ? 'SUSPENDED' : 'RUNNING')
            })))
            setError(null)
        } catch (err) {
            console.error(err)
            setError('Failed to fetch process instances')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="p-8 animate-fade-in">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Process Instances</h1>
                    <p className="text-slate-400">Track running and completed workflows</p>
                </div>
                <button
                    onClick={fetchInstances}
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

            <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
                <table className="w-full text-left">
                    <thead className="bg-slate-900/50 text-slate-400 text-sm">
                        <tr>
                            <th className="p-4 font-medium">ID</th>
                            <th className="p-4 font-medium">Process Definition</th>
                            <th className="p-4 font-medium">Status</th>
                            <th className="p-4 font-medium">Started</th>
                            <th className="p-4 font-medium">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-700">
                        {instances.map((instance) => (
                            <tr key={instance.id} className="text-slate-300 hover:bg-slate-700/50 transition-colors">
                                <td className="p-4 font-mono text-sm">{instance.id.substring(0, 8)}...</td>
                                <td className="p-4">
                                    <div className="flex items-center gap-2">
                                        <FileText size={16} className="text-blue-400" />
                                        <span>{instance.processDefinitionKey}</span>
                                    </div>
                                </td>
                                <td className="p-4">
                                    <StatusBadge status={instance.status} />
                                </td>
                                <td className="p-4 text-sm text-slate-400">
                                    {instance.startTime ? format(new Date(instance.startTime), 'MMM d, HH:mm') : '-'}
                                </td>
                                <td className="p-4">
                                    <Link
                                        to={`/modeler/instances/${instance.id}`}
                                        className="p-2 hover:bg-blue-600/20 text-blue-400 rounded-lg transition-colors inline-block"
                                        title="View Details"
                                    >
                                        <Eye size={18} />
                                    </Link>
                                </td>
                            </tr>
                        ))}
                        {instances.length === 0 && !loading && (
                            <tr>
                                <td colSpan={5} className="p-8 text-center text-slate-500">
                                    No process instances found
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

function StatusBadge({ status }: { status: ProcessInstance['status'] }) {
    const styles = {
        RUNNING: 'bg-green-500/10 text-green-400 border-green-500/20',
        COMPLETED: 'bg-slate-500/10 text-slate-400 border-slate-500/20',
        SUSPENDED: 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20',
    }

    return (
        <span className={`px-2.5 py-1 rounded-full text-xs font-medium border ${styles[status]}`}>
            {status}
        </span>
    )
}
