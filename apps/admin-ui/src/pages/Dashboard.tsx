import { useState, useEffect } from 'react'
import { Activity, CheckCircle, Clock, AlertTriangle, TrendingUp, Users, RefreshCw } from 'lucide-react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'

export default function Dashboard() {
    const [stats, setStats] = useState({
        definitions: 0,
        instances: 0,
        deployments: 0,
        tasks: 0
    })
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        fetchStats()
    }, [])

    const fetchStats = async () => {
        setLoading(true)
        try {
            // Fetch counts from various services
            const [defRes, instRes, depRes] = await Promise.all([
                fetch('/api/v1/process-definitions/count'),
                fetch('/api/v1/process-instances/count'),
                fetch('/api/v1/deployments/count')
            ])

            const defData = await defRes.json()
            const instData = await instRes.json()
            const depData = await depRes.json()

            // Task count would come from task-service if available, or mocked for now if endpoint missing
            // const taskRes = await fetch('/api/v1/tasks/count') 
            // const taskData = await taskRes.json()

            setStats({
                definitions: defData.count || 0,
                instances: instData.count || 0,
                deployments: depData.count || 0,
                tasks: 0 // Placeholder until task count endpoint exists
            })
            setError(null)
        } catch (err) {
            console.error(err)
            setError('Failed to fetch system stats')
        } finally {
            setLoading(false)
        }
    }

    const statCards = [
        { label: 'Process Definitions', value: stats.definitions, icon: Activity, color: 'text-blue-400', bg: 'bg-blue-600/20' },
        { label: 'Active Instances', value: stats.instances, icon: CheckCircle, color: 'text-green-400', bg: 'bg-green-600/20' },
        { label: 'Total Deployments', value: stats.deployments, icon: Clock, color: 'text-yellow-400', bg: 'bg-yellow-600/20' },
        { label: 'Active Tasks', value: stats.tasks, icon: AlertTriangle, color: 'text-purple-400', bg: 'bg-purple-600/20' },
    ]

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="mb-8 flex justify-between items-end">
                <div>
                    <h1 className="text-3xl font-bold text-white mb-2">System Dashboard</h1>
                    <p className="text-slate-400">Monitor and manage your workflow platform</p>
                </div>
                <button
                    onClick={fetchStats}
                    className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg transition-colors"
                >
                    <RefreshCw size={20} className={loading ? "animate-spin" : ""} />
                </button>
            </div>

            {/* Error Banner */}
            {error && (
                <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-400 flex items-center gap-2">
                    <AlertTriangle size={20} />
                    {error}
                </div>
            )}

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {statCards.map((stat) => (
                    <div
                        key={stat.label}
                        className="bg-slate-800 rounded-xl p-6 border border-slate-700"
                    >
                        <div className="flex items-center justify-between mb-4">
                            <div className={`p-3 rounded-lg ${stat.bg}`}>
                                <stat.icon size={24} className={stat.color} />
                            </div>
                        </div>
                        <p className="text-3xl font-bold text-white mb-1">
                            {loading ? '...' : stat.value}
                        </p>
                        <p className="text-sm text-slate-400">{stat.label}</p>
                    </div>
                ))}
            </div>

            {/* Note: Charts are kept static/placeholder for now as historical data API is not yet implemented */}
            <div className="bg-slate-800 rounded-xl p-6 border border-slate-700 mb-8">
                <div className="flex items-center justify-center h-48 text-slate-500">
                    <p>Historical charts require Metrics Service integration</p>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                    <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                        <Users size={20} />
                        Active Users
                    </h2>
                    <div className="flex items-center gap-4">
                        <div className="text-4xl font-bold text-white">1</div>
                        <div className="text-sm text-slate-400">users online (Admin)</div>
                    </div>
                </div>

                <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                    <h2 className="text-lg font-semibold text-white mb-4">System Health</h2>
                    <div className="flex items-center gap-4">
                        <div className="flex-1 h-3 bg-slate-700 rounded-full overflow-hidden">
                            <div className="h-full bg-green-500 rounded-full" style={{ width: error ? '50%' : '100%' }} />
                        </div>
                        <span className={`font-medium ${error ? 'text-yellow-400' : 'text-green-400'}`}>
                            {error ? 'Degraded' : 'Healthy'}
                        </span>
                    </div>
                    <p className="text-sm text-slate-400 mt-2">
                        {error ? 'Some services unreachable' : 'All services operating normally'}
                    </p>
                </div>
            </div>
        </div>
    )
}
