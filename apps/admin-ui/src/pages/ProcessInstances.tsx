import { useState } from 'react'
import { Search, Filter, Play, Pause, XCircle, Eye, MoreVertical } from 'lucide-react'

interface ProcessInstance {
    id: string
    processKey: string
    processName: string
    status: 'RUNNING' | 'SUSPENDED' | 'COMPLETED' | 'TERMINATED'
    startedAt: string
    startedBy: string
    variables: Record<string, unknown>
}

const mockInstances: ProcessInstance[] = [
    { id: 'inst-001', processKey: 'loan-approval', processName: 'Loan Approval', status: 'RUNNING', startedAt: '2024-01-15T10:00:00', startedBy: 'john.doe', variables: { amount: 50000 } },
    { id: 'inst-002', processKey: 'expense-approval', processName: 'Expense Approval', status: 'RUNNING', startedAt: '2024-01-15T09:30:00', startedBy: 'jane.smith', variables: { amount: 500 } },
    { id: 'inst-003', processKey: 'employee-onboarding', processName: 'Employee Onboarding', status: 'SUSPENDED', startedAt: '2024-01-14T14:00:00', startedBy: 'admin', variables: { employeeName: 'Bob' } },
    { id: 'inst-004', processKey: 'loan-approval', processName: 'Loan Approval', status: 'COMPLETED', startedAt: '2024-01-14T11:00:00', startedBy: 'john.doe', variables: { amount: 25000 } },
    { id: 'inst-005', processKey: 'customer-support', processName: 'Customer Support', status: 'TERMINATED', startedAt: '2024-01-13T16:00:00', startedBy: 'support', variables: { ticketId: 'TICK-123' } },
]

const statusColors = {
    RUNNING: 'bg-green-500/20 text-green-400',
    SUSPENDED: 'bg-yellow-500/20 text-yellow-400',
    COMPLETED: 'bg-blue-500/20 text-blue-400',
    TERMINATED: 'bg-red-500/20 text-red-400',
}

export default function ProcessInstances() {
    const [search, setSearch] = useState('')
    const [statusFilter, setStatusFilter] = useState<string>('all')

    const filteredInstances = mockInstances.filter((inst) => {
        const matchesSearch = inst.processName.toLowerCase().includes(search.toLowerCase()) ||
            inst.id.toLowerCase().includes(search.toLowerCase())
        const matchesStatus = statusFilter === 'all' || inst.status === statusFilter
        return matchesSearch && matchesStatus
    })

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Process Instances</h1>
                    <p className="text-slate-400">{mockInstances.length} total instances</p>
                </div>
            </div>

            {/* Filters */}
            <div className="flex gap-4 mb-6">
                <div className="flex-1 relative">
                    <Search size={20} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search by ID or process name..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
                    />
                </div>

                <div className="flex items-center gap-2 bg-slate-800 rounded-lg p-1 border border-slate-700">
                    <Filter size={18} className="text-slate-400 ml-2" />
                    {(['all', 'RUNNING', 'SUSPENDED', 'COMPLETED', 'TERMINATED'] as const).map((s) => (
                        <button
                            key={s}
                            onClick={() => setStatusFilter(s)}
                            className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${statusFilter === s
                                    ? 'bg-indigo-600 text-white'
                                    : 'text-slate-400 hover:text-white'
                                }`}
                        >
                            {s === 'all' ? 'All' : s}
                        </button>
                    ))}
                </div>
            </div>

            {/* Table */}
            <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
                <table className="w-full">
                    <thead className="bg-slate-900">
                        <tr>
                            <th className="text-left px-6 py-4 text-sm font-medium text-slate-400">Instance ID</th>
                            <th className="text-left px-6 py-4 text-sm font-medium text-slate-400">Process</th>
                            <th className="text-left px-6 py-4 text-sm font-medium text-slate-400">Status</th>
                            <th className="text-left px-6 py-4 text-sm font-medium text-slate-400">Started</th>
                            <th className="text-left px-6 py-4 text-sm font-medium text-slate-400">Started By</th>
                            <th className="text-right px-6 py-4 text-sm font-medium text-slate-400">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-700">
                        {filteredInstances.map((inst) => (
                            <tr key={inst.id} className="hover:bg-slate-700/50">
                                <td className="px-6 py-4">
                                    <span className="text-white font-mono text-sm">{inst.id}</span>
                                </td>
                                <td className="px-6 py-4">
                                    <span className="text-white">{inst.processName}</span>
                                    <p className="text-xs text-slate-400">{inst.processKey}</p>
                                </td>
                                <td className="px-6 py-4">
                                    <span className={`px-2 py-1 text-xs rounded-full ${statusColors[inst.status]}`}>
                                        {inst.status}
                                    </span>
                                </td>
                                <td className="px-6 py-4 text-slate-300 text-sm">
                                    {new Date(inst.startedAt).toLocaleString()}
                                </td>
                                <td className="px-6 py-4 text-slate-300 text-sm">{inst.startedBy}</td>
                                <td className="px-6 py-4">
                                    <div className="flex items-center justify-end gap-2">
                                        <button className="p-1.5 hover:bg-slate-600 rounded text-slate-400 hover:text-white" title="View">
                                            <Eye size={16} />
                                        </button>
                                        {inst.status === 'RUNNING' && (
                                            <button className="p-1.5 hover:bg-slate-600 rounded text-slate-400 hover:text-yellow-400" title="Suspend">
                                                <Pause size={16} />
                                            </button>
                                        )}
                                        {inst.status === 'SUSPENDED' && (
                                            <button className="p-1.5 hover:bg-slate-600 rounded text-slate-400 hover:text-green-400" title="Resume">
                                                <Play size={16} />
                                            </button>
                                        )}
                                        {(inst.status === 'RUNNING' || inst.status === 'SUSPENDED') && (
                                            <button className="p-1.5 hover:bg-slate-600 rounded text-slate-400 hover:text-red-400" title="Terminate">
                                                <XCircle size={16} />
                                            </button>
                                        )}
                                        <button className="p-1.5 hover:bg-slate-600 rounded text-slate-400 hover:text-white" title="More">
                                            <MoreVertical size={16} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}
