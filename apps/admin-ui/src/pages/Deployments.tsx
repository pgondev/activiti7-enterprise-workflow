import { Upload, Package, Trash2, Clock, FileCode } from 'lucide-react'

interface Deployment {
    id: string
    name: string
    deployedAt: string
    deployedBy: string
    resources: string[]
    processCount: number
}

const mockDeployments: Deployment[] = [
    { id: 'dep-001', name: 'loan-approval-v3', deployedAt: '2024-01-15T10:00:00', deployedBy: 'admin', resources: ['loan-approval.bpmn', 'credit-check.dmn'], processCount: 2 },
    { id: 'dep-002', name: 'expense-approval-v5', deployedAt: '2024-01-14T14:00:00', deployedBy: 'admin', resources: ['expense-approval.bpmn'], processCount: 1 },
    { id: 'dep-003', name: 'employee-onboarding-v2', deployedAt: '2024-01-13T09:00:00', deployedBy: 'jane.smith', resources: ['employee-onboarding.bpmn', 'forms.json'], processCount: 1 },
    { id: 'dep-004', name: 'customer-support-v1', deployedAt: '2024-01-12T16:00:00', deployedBy: 'admin', resources: ['customer-support.bpmn'], processCount: 1 },
]

export default function Deployments() {
    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">Deployments</h1>
                    <p className="text-slate-400">{mockDeployments.length} deployments</p>
                </div>
                <button className="flex items-center gap-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors">
                    <Upload size={18} />
                    Deploy New
                </button>
            </div>

            {/* Deployments Grid */}
            <div className="grid gap-4">
                {mockDeployments.map((dep) => (
                    <div
                        key={dep.id}
                        className="bg-slate-800 rounded-xl border border-slate-700 p-5 hover:border-slate-600 transition-colors"
                    >
                        <div className="flex items-start justify-between">
                            <div className="flex items-start gap-4">
                                <div className="p-3 bg-indigo-600/20 rounded-lg">
                                    <Package size={24} className="text-indigo-400" />
                                </div>
                                <div>
                                    <h3 className="text-lg font-semibold text-white">{dep.name}</h3>
                                    <p className="text-sm text-slate-400 font-mono">{dep.id}</p>

                                    <div className="flex items-center gap-4 mt-3 text-sm text-slate-400">
                                        <span className="flex items-center gap-1">
                                            <Clock size={14} />
                                            {new Date(dep.deployedAt).toLocaleString()}
                                        </span>
                                        <span>by {dep.deployedBy}</span>
                                        <span>{dep.processCount} process(es)</span>
                                    </div>

                                    <div className="flex gap-2 mt-3">
                                        {dep.resources.map((res) => (
                                            <span
                                                key={res}
                                                className="flex items-center gap-1 px-2 py-1 bg-slate-700 rounded text-xs text-slate-300"
                                            >
                                                <FileCode size={12} />
                                                {res}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            </div>

                            <button className="p-2 hover:bg-red-600/20 rounded text-slate-400 hover:text-red-400 transition-colors" title="Delete">
                                <Trash2 size={18} />
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}
