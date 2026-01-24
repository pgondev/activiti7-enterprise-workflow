import { Link } from 'react-router-dom'
import { FileCode, Table2, Plus, Activity, Clock, CheckCircle } from 'lucide-react'

const stats = [
    { label: 'Active Processes', value: '24', icon: Activity, color: 'text-green-400' },
    { label: 'Pending Tasks', value: '156', icon: Clock, color: 'text-yellow-400' },
    { label: 'Completed Today', value: '89', icon: CheckCircle, color: 'text-blue-400' },
]

const recentItems = [
    { name: 'Employee Onboarding', type: 'BPMN', updated: '2 hours ago' },
    { name: 'Loan Approval', type: 'BPMN', updated: '5 hours ago' },
    { name: 'Risk Assessment', type: 'DMN', updated: '1 day ago' },
    { name: 'Expense Approval', type: 'BPMN', updated: '2 days ago' },
]

export default function Dashboard() {
    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-white mb-2">Dashboard</h1>
                    <p className="text-slate-400">Welcome to the Workflow Modeler</p>
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

            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                {stats.map((stat) => (
                    <div
                        key={stat.label}
                        className="bg-slate-800 rounded-xl p-6 border border-slate-700 hover:border-slate-600 transition-colors"
                    >
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-slate-400 text-sm mb-1">{stat.label}</p>
                                <p className="text-3xl font-bold text-white">{stat.value}</p>
                            </div>
                            <div className={`p-3 rounded-lg bg-slate-700 ${stat.color}`}>
                                <stat.icon size={24} />
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Quick Actions */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                <div className="bg-gradient-to-br from-blue-600 to-blue-800 rounded-xl p-6 cursor-pointer hover:shadow-xl hover:shadow-blue-600/20 transition-shadow">
                    <Link to="/modeler/bpmn" className="flex items-center gap-4">
                        <div className="p-4 bg-white/20 rounded-lg">
                            <FileCode size={32} className="text-white" />
                        </div>
                        <div>
                            <h3 className="text-xl font-semibold text-white">BPMN Modeler</h3>
                            <p className="text-blue-200">Design business processes visually</p>
                        </div>
                    </Link>
                </div>
                <div className="bg-gradient-to-br from-indigo-600 to-indigo-800 rounded-xl p-6 cursor-pointer hover:shadow-xl hover:shadow-indigo-600/20 transition-shadow">
                    <Link to="/modeler/dmn" className="flex items-center gap-4">
                        <div className="p-4 bg-white/20 rounded-lg">
                            <Table2 size={32} className="text-white" />
                        </div>
                        <div>
                            <h3 className="text-xl font-semibold text-white">DMN Modeler</h3>
                            <p className="text-indigo-200">Create decision tables and rules</p>
                        </div>
                    </Link>
                </div>
            </div>

            {/* Recent Processes */}
            <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
                <div className="p-4 border-b border-slate-700">
                    <h2 className="text-lg font-semibold text-white">Recent Processes</h2>
                </div>
                <div className="divide-y divide-slate-700">
                    {recentItems.map((item, index) => (
                        <div
                            key={index}
                            className="p-4 hover:bg-slate-700/50 transition-colors cursor-pointer flex items-center justify-between"
                        >
                            <div className="flex items-center gap-3">
                                {item.type === 'BPMN' ? (
                                    <FileCode size={20} className="text-blue-400" />
                                ) : (
                                    <Table2 size={20} className="text-indigo-400" />
                                )}
                                <span className="text-white font-medium">{item.name}</span>
                                <span className="px-2 py-0.5 text-xs bg-slate-700 rounded text-slate-300">
                                    {item.type}
                                </span>
                            </div>
                            <span className="text-slate-400 text-sm">{item.updated}</span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}
