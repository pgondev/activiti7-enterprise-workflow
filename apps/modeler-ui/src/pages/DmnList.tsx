import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Search, Plus, Table2, MoreVertical, RefreshCw, FileCode2 } from 'lucide-react'
import { decisionApi } from '../api/client'

interface DecisionDefinition {
    id: string
    key: string
    name: string
    version: number
    deploymentId: string
    category: string | null
}

export default function DmnList() {
    const [decisions, setDecisions] = useState<DecisionDefinition[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [search, setSearch] = useState('')

    useEffect(() => {
        fetchDecisions()
    }, [])

    const fetchDecisions = async () => {
        setLoading(true)
        setError(null)
        try {
            const response = await decisionApi.getDecisions({ page: 0, size: 50 })
            const data = response.data
            // Handle Page format
            setDecisions(data.content || data || [])
        } catch (err) {
            console.error('Failed to fetch decisions:', err)
            setError('Failed to connect to decision engine')
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async (deploymentId: string) => {
        if (!confirm('Are you sure you want to delete this decision table?')) return
        try {
            await decisionApi.delete(deploymentId)
            fetchDecisions()
        } catch (err: any) {
            console.error('Failed to delete deployment:', err)
            alert('Failed to delete deployment')
        }
    }

    const filteredDecisions = decisions.filter((d) => {
        return (d.name || d.key).toLowerCase().includes(search.toLowerCase())
    })

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Decision Tables</h1>
                    <p className="text-slate-400">
                        {decisions.length} decision tables deployed
                    </p>
                </div>
                <div className="flex gap-3">
                    <button
                        onClick={fetchDecisions}
                        className="flex items-center gap-2 px-4 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors"
                        title="Refresh"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
                        Refresh
                    </button>
                    <Link
                        to="/modeler/dmn"
                        className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors"
                    >
                        <Plus size={18} />
                        New Decision
                    </Link>
                </div>
            </div>

            {/* Error Banner */}
            {error && (
                <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-400">
                    ⚠️ {error}
                </div>
            )}

            {/* Filters */}
            <div className="flex gap-4 mb-6">
                <div className="flex-1 relative">
                    <Search size={20} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search decision tables..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
                    />
                </div>
            </div>

            {/* Grid */}
            {!loading && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {filteredDecisions.map((decision) => (
                        <div
                            key={decision.id}
                            className="bg-slate-800 rounded-xl border border-slate-700 p-5 hover:border-indigo-500/50 transition-all group"
                        >
                            <div className="flex items-start justify-between mb-3">
                                <div className="p-2 bg-indigo-600/20 rounded-lg">
                                    <Table2 size={20} className="text-indigo-400" />
                                </div>
                                <div className="flex gap-1">
                                    <Link
                                        to={`/modeler/dmn/${decision.id}`}
                                        className="p-1.5 hover:bg-slate-700 rounded text-slate-400 hover:text-indigo-400 transition-colors"
                                        title="Edit Decision"
                                    >
                                        <FileCode2 size={16} />
                                    </Link>
                                    <button
                                        className="p-1.5 hover:bg-slate-700 rounded text-slate-400 hover:text-red-400 transition-colors"
                                        title="Delete"
                                        onClick={() => handleDelete(decision.deploymentId)}
                                    >
                                        <MoreVertical size={16} />
                                    </button>
                                </div>
                            </div>

                            <h3 className="font-semibold text-white mb-1 group-hover:text-indigo-400 transition-colors">
                                {decision.name || decision.key}
                            </h3>
                            <div className="flex items-center gap-3 text-xs text-slate-500 mt-4">
                                <span className="px-2 py-0.5 bg-slate-700 rounded">v{decision.version}</span>
                                <span className="font-mono">{decision.key}</span>
                            </div>
                        </div>
                    ))}

                    {filteredDecisions.length === 0 && !loading && (
                        <div className="col-span-3 text-center py-12 text-slate-400">
                            <Table2 size={48} className="mx-auto mb-4 opacity-50" />
                            <p className="text-lg">No decisions found</p>
                            <p className="text-sm mt-1">Create a decision table to get started</p>
                        </div>
                    )}
                </div>
            )}

            {loading && (
                <div className="text-center py-12 text-slate-400">
                    <RefreshCw size={32} className="mx-auto mb-4 animate-spin" />
                    <p>Loading decisions...</p>
                </div>
            )}
        </div>
    )
}
