import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Search, Plus, FileCode2, MoreVertical, Play, RefreshCw } from 'lucide-react'
import { processApi, deploymentApi } from '../api/client'

interface ProcessDefinition {
    id: string
    key: string
    name: string
    version: number
    deploymentId: string
    description: string | null
    category: string | null
}

export default function ProcessList() {
    const [processes, setProcesses] = useState<ProcessDefinition[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [search, setSearch] = useState('')
    const [typeFilter, setTypeFilter] = useState<string>('all')

    useEffect(() => {
        fetchProcesses()
    }, [])

    const fetchProcesses = async () => {
        setLoading(true)
        setError(null)
        try {
            const response = await processApi.getDefinitions({ page: 0, size: 50 })
            setProcesses(response.data || [])
        } catch (err) {
            console.error('Failed to fetch processes:', err)
            setError('Failed to connect to backend')
            // Use mock data as fallback
            setProcesses([
                { id: 'proc:1', key: 'loan-approval', name: 'Loan Approval Process', version: 1, deploymentId: 'dep-1', description: 'Approval workflow for loan applications', category: 'finance' },
                { id: 'proc:2', key: 'employee-onboarding', name: 'Employee Onboarding', version: 2, deploymentId: 'dep-2', description: 'New hire onboarding process', category: 'hr' },
                { id: 'proc:3', key: 'expense-approval', name: 'Expense Approval', version: 1, deploymentId: 'dep-3', description: 'Expense report approval workflow', category: 'finance' },
            ])
        } finally {
            setLoading(false)
        }
    }

    const handleStartProcess = async (processKey: string) => {
        try {
            await processApi.startProcess(processKey, {})
            alert(`Process ${processKey} started successfully!`)
        } catch (err) {
            console.error('Failed to start process:', err)
            alert('Failed to start process')
        }
    }

    const handleDelete = async (deploymentId: string) => {
        if (!confirm('Are you sure you want to delete this process definition? This will delete the deployment.')) return
        try {
            await deploymentApi.delete(deploymentId)
            fetchProcesses()
        } catch (err: any) {
            console.error('Failed to delete deployment:', err)
            alert('Failed to delete deployment')
        }
    }

    const filteredProcesses = processes.filter((p) => {
        const matchesSearch = (p.name || p.key).toLowerCase().includes(search.toLowerCase())
        return matchesSearch
    })

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Process Definitions</h1>
                    <p className="text-slate-400">
                        {processes.length} process definitions deployed
                    </p>
                </div>
                <div className="flex gap-3">
                    <button
                        onClick={fetchProcesses}
                        className="flex items-center gap-2 px-4 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors"
                        title="Refresh"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
                        Refresh
                    </button>
                    <Link
                        to="/modeler/bpmn"
                        className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
                    >
                        <Plus size={18} />
                        New Process
                    </Link>
                </div>
            </div>

            {/* Error Banner */}
            {error && (
                <div className="mb-6 p-4 bg-yellow-500/20 border border-yellow-500/50 rounded-lg text-yellow-400">
                    ⚠️ {error} - Showing demo data
                </div>
            )}

            {/* Filters */}
            <div className="flex gap-4 mb-6">
                <div className="flex-1 relative">
                    <Search size={20} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search processes..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-blue-500"
                        aria-label="Search processes"
                    />
                </div>

                <div className="flex items-center gap-2 bg-slate-800 rounded-lg p-1 border border-slate-700">
                    {(['all', 'bpmn', 'dmn'] as const).map((type) => (
                        <button
                            key={type}
                            onClick={() => setTypeFilter(type)}
                            className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${typeFilter === type
                                ? 'bg-blue-600 text-white'
                                : 'text-slate-400 hover:text-white'
                                }`}
                        >
                            {type.toUpperCase()}
                        </button>
                    ))}
                </div>
            </div>

            {/* Loading State */}
            {loading && (
                <div className="text-center py-12 text-slate-400">
                    <RefreshCw size={32} className="mx-auto mb-4 animate-spin" />
                    <p>Loading processes...</p>
                </div>
            )}

            {/* Process Grid */}
            {!loading && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {filteredProcesses.map((process) => (
                        <div
                            key={process.id}
                            className="bg-slate-800 rounded-xl border border-slate-700 p-5 hover:border-blue-500/50 transition-all group"
                        >
                            <div className="flex items-start justify-between mb-3">
                                <div className="p-2 bg-blue-600/20 rounded-lg">
                                    <FileCode2 size={20} className="text-blue-400" />
                                </div>
                                <div className="flex gap-1">
                                    <button
                                        onClick={() => handleStartProcess(process.key)}
                                        className="p-1.5 hover:bg-slate-700 rounded text-slate-400 hover:text-green-400 transition-colors"
                                        title="Start Instance"
                                    >
                                        <Play size={16} />
                                    </button>
                                    <Link
                                        to={`/modeler/bpmn/${process.id}`}
                                        className="p-1.5 hover:bg-slate-700 rounded text-slate-400 hover:text-blue-400 transition-colors"
                                        title="Edit Process"
                                    >
                                        <FileCode2 size={16} />
                                    </Link>
                                    <button
                                        className="p-1.5 hover:bg-slate-700 rounded text-slate-400 hover:text-red-400 transition-colors"
                                        title="Delete"
                                        onClick={() => handleDelete(process.deploymentId)}
                                    >
                                        <MoreVertical size={16} />
                                    </button>
                                </div>
                            </div>

                            <h3 className="font-semibold text-white mb-1 group-hover:text-blue-400 transition-colors">
                                {process.name || process.key}
                            </h3>
                            <p className="text-sm text-slate-400 mb-3 line-clamp-2">
                                {process.description || 'No description'}
                            </p>

                            <div className="flex items-center gap-3 text-xs text-slate-500">
                                <span className="px-2 py-0.5 bg-slate-700 rounded">v{process.version}</span>
                                <span>{process.key}</span>
                            </div>
                        </div>
                    ))}

                    {filteredProcesses.length === 0 && !loading && (
                        <div className="col-span-3 text-center py-12 text-slate-400">
                            <FileCode2 size={48} className="mx-auto mb-4 opacity-50" />
                            <p className="text-lg">No processes found</p>
                            <p className="text-sm mt-1">Deploy a BPMN process to get started</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}
