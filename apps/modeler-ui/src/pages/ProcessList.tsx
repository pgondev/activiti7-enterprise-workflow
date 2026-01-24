import { useState } from 'react'
import { Link } from 'react-router-dom'
import { FileCode, Table2, Plus, Search, MoreVertical, Play, Trash2, Copy, Edit } from 'lucide-react'

interface ProcessDefinition {
    id: string
    key: string
    name: string
    type: 'BPMN' | 'DMN'
    version: number
    deployedAt: string
    instances: number
}

const mockProcesses: ProcessDefinition[] = [
    { id: '1', key: 'employee-onboarding', name: 'Employee Onboarding', type: 'BPMN', version: 3, deployedAt: '2024-01-15', instances: 45 },
    { id: '2', key: 'loan-approval', name: 'Loan Approval Process', type: 'BPMN', version: 5, deployedAt: '2024-01-10', instances: 128 },
    { id: '3', key: 'risk-assessment', name: 'Risk Assessment', type: 'DMN', version: 2, deployedAt: '2024-01-12', instances: 89 },
    { id: '4', key: 'expense-approval', name: 'Expense Approval', type: 'BPMN', version: 4, deployedAt: '2024-01-08', instances: 234 },
    { id: '5', key: 'discount-rules', name: 'Discount Rules', type: 'DMN', version: 1, deployedAt: '2024-01-05', instances: 567 },
    { id: '6', key: 'customer-support', name: 'Customer Support Ticket', type: 'BPMN', version: 2, deployedAt: '2024-01-03', instances: 789 },
]

export default function ProcessList() {
    const [search, setSearch] = useState('')
    const [filter, setFilter] = useState<'all' | 'BPMN' | 'DMN'>('all')

    const filteredProcesses = mockProcesses.filter((p) => {
        const matchesSearch = p.name.toLowerCase().includes(search.toLowerCase()) ||
            p.key.toLowerCase().includes(search.toLowerCase())
        const matchesFilter = filter === 'all' || p.type === filter
        return matchesSearch && matchesFilter
    })

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-white mb-2">Process Definitions</h1>
                    <p className="text-slate-400">Manage your BPMN and DMN definitions</p>
                </div>
                <div className="flex gap-3">
                    <Link
                        to="/modeler/bpmn"
                        className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors shadow-lg shadow-blue-600/30"
                    >
                        <Plus size={18} />
                        New BPMN
                    </Link>
                    <Link
                        to="/modeler/dmn"
                        className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors shadow-lg shadow-indigo-600/30"
                    >
                        <Plus size={18} />
                        New DMN
                    </Link>
                </div>
            </div>

            {/* Filters */}
            <div className="flex gap-4 mb-6">
                <div className="flex-1 relative">
                    <Search size={20} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search processes..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-blue-500"
                    />
                </div>
                <div className="flex bg-slate-800 rounded-lg p-1 border border-slate-700">
                    {(['all', 'BPMN', 'DMN'] as const).map((f) => (
                        <button
                            key={f}
                            onClick={() => setFilter(f)}
                            className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${filter === f
                                    ? 'bg-blue-600 text-white'
                                    : 'text-slate-400 hover:text-white'
                                }`}
                        >
                            {f === 'all' ? 'All' : f}
                        </button>
                    ))}
                </div>
            </div>

            {/* Process Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {filteredProcesses.map((process) => (
                    <div
                        key={process.id}
                        className="bg-slate-800 rounded-xl border border-slate-700 p-5 hover:border-slate-600 transition-colors group"
                    >
                        <div className="flex items-start justify-between mb-4">
                            <div className="flex items-center gap-3">
                                <div className={`p-2 rounded-lg ${process.type === 'BPMN' ? 'bg-blue-600/20 text-blue-400' : 'bg-indigo-600/20 text-indigo-400'
                                    }`}>
                                    {process.type === 'BPMN' ? <FileCode size={20} /> : <Table2 size={20} />}
                                </div>
                                <div>
                                    <h3 className="font-semibold text-white">{process.name}</h3>
                                    <p className="text-sm text-slate-400">{process.key}</p>
                                </div>
                            </div>
                            <button className="p-1 hover:bg-slate-700 rounded opacity-0 group-hover:opacity-100 transition-opacity">
                                <MoreVertical size={18} className="text-slate-400" />
                            </button>
                        </div>

                        <div className="flex items-center gap-4 text-sm text-slate-400 mb-4">
                            <span>v{process.version}</span>
                            <span>•</span>
                            <span>{process.instances} instances</span>
                            <span>•</span>
                            <span>{process.deployedAt}</span>
                        </div>

                        <div className="flex gap-2">
                            <Link
                                to={`/modeler/${process.type.toLowerCase()}/${process.id}`}
                                className="flex-1 flex items-center justify-center gap-2 py-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors text-sm"
                            >
                                <Edit size={16} />
                                Edit
                            </Link>
                            <button className="p-2 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors">
                                <Play size={16} />
                            </button>
                            <button className="p-2 bg-slate-700 hover:bg-slate-600 text-slate-400 hover:text-white rounded-lg transition-colors">
                                <Copy size={16} />
                            </button>
                            <button className="p-2 bg-slate-700 hover:bg-red-600 text-slate-400 hover:text-white rounded-lg transition-colors">
                                <Trash2 size={16} />
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}
