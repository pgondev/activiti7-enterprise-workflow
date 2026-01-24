import { Activity, CheckCircle, Clock, AlertTriangle, TrendingUp, Users } from 'lucide-react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'

const stats = [
    { label: 'Active Instances', value: '1,234', change: '+12%', icon: Activity, color: 'text-blue-400', bg: 'bg-blue-600/20' },
    { label: 'Completed Today', value: '456', change: '+8%', icon: CheckCircle, color: 'text-green-400', bg: 'bg-green-600/20' },
    { label: 'Pending Tasks', value: '89', change: '-5%', icon: Clock, color: 'text-yellow-400', bg: 'bg-yellow-600/20' },
    { label: 'Failed Instances', value: '12', change: '+2', icon: AlertTriangle, color: 'text-red-400', bg: 'bg-red-600/20' },
]

const chartData = [
    { name: 'Mon', completed: 120, started: 150 },
    { name: 'Tue', completed: 180, started: 200 },
    { name: 'Wed', completed: 150, started: 180 },
    { name: 'Thu', completed: 220, started: 240 },
    { name: 'Fri', completed: 280, started: 300 },
    { name: 'Sat', completed: 100, started: 120 },
    { name: 'Sun', completed: 80, started: 90 },
]

const processDistribution = [
    { name: 'Loan Approval', value: 35, color: '#3b82f6' },
    { name: 'Employee Onboarding', value: 25, color: '#8b5cf6' },
    { name: 'Expense Approval', value: 20, color: '#22c55e' },
    { name: 'Customer Support', value: 20, color: '#f59e0b' },
]

export default function Dashboard() {
    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-white mb-2">System Dashboard</h1>
                <p className="text-slate-400">Monitor and manage your workflow platform</p>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {stats.map((stat) => (
                    <div
                        key={stat.label}
                        className="bg-slate-800 rounded-xl p-6 border border-slate-700"
                    >
                        <div className="flex items-center justify-between mb-4">
                            <div className={`p-3 rounded-lg ${stat.bg}`}>
                                <stat.icon size={24} className={stat.color} />
                            </div>
                            <span className={`text-sm ${stat.change.startsWith('+') ? 'text-green-400' : 'text-red-400'}`}>
                                {stat.change}
                            </span>
                        </div>
                        <p className="text-3xl font-bold text-white mb-1">{stat.value}</p>
                        <p className="text-sm text-slate-400">{stat.label}</p>
                    </div>
                ))}
            </div>

            {/* Charts Row */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
                {/* Area Chart */}
                <div className="lg:col-span-2 bg-slate-800 rounded-xl p-6 border border-slate-700">
                    <div className="flex items-center justify-between mb-6">
                        <h2 className="text-lg font-semibold text-white flex items-center gap-2">
                            <TrendingUp size={20} />
                            Process Activity
                        </h2>
                        <select className="bg-slate-700 text-white text-sm rounded-lg px-3 py-1.5 border border-slate-600">
                            <option>Last 7 days</option>
                            <option>Last 30 days</option>
                            <option>Last 90 days</option>
                        </select>
                    </div>
                    <div className="h-64">
                        <ResponsiveContainer width="100%" height="100%">
                            <AreaChart data={chartData}>
                                <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                                <XAxis dataKey="name" stroke="#64748b" />
                                <YAxis stroke="#64748b" />
                                <Tooltip
                                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                                    labelStyle={{ color: '#f8fafc' }}
                                />
                                <Area type="monotone" dataKey="started" stroke="#8b5cf6" fill="#8b5cf6" fillOpacity={0.3} />
                                <Area type="monotone" dataKey="completed" stroke="#22c55e" fill="#22c55e" fillOpacity={0.3} />
                            </AreaChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Pie Chart */}
                <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                    <h2 className="text-lg font-semibold text-white mb-6">Process Distribution</h2>
                    <div className="h-48">
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie
                                    data={processDistribution}
                                    innerRadius={50}
                                    outerRadius={80}
                                    paddingAngle={2}
                                    dataKey="value"
                                >
                                    {processDistribution.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={entry.color} />
                                    ))}
                                </Pie>
                                <Tooltip
                                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                                />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                    <div className="space-y-2 mt-4">
                        {processDistribution.map((item) => (
                            <div key={item.name} className="flex items-center justify-between text-sm">
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }} />
                                    <span className="text-slate-300">{item.name}</span>
                                </div>
                                <span className="text-white font-medium">{item.value}%</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                    <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                        <Users size={20} />
                        Active Users
                    </h2>
                    <div className="flex items-center gap-4">
                        <div className="text-4xl font-bold text-white">156</div>
                        <div className="text-sm text-slate-400">users online</div>
                    </div>
                </div>

                <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                    <h2 className="text-lg font-semibold text-white mb-4">System Health</h2>
                    <div className="flex items-center gap-4">
                        <div className="flex-1 h-3 bg-slate-700 rounded-full overflow-hidden">
                            <div className="h-full bg-green-500 rounded-full" style={{ width: '94%' }} />
                        </div>
                        <span className="text-green-400 font-medium">94%</span>
                    </div>
                    <p className="text-sm text-slate-400 mt-2">All services operating normally</p>
                </div>
            </div>
        </div>
    )
}
