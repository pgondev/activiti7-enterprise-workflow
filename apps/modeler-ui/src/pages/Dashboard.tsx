import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { LayoutDashboard, FileCode2, Table2, Plus, Activity, CheckCircle, Clock, FileText } from 'lucide-react'
import { processApi, instanceApi } from '../api/client'

interface Stats {
    definitions: number
    instances: number
    loading: boolean
    error: string | null
}

export default function Dashboard() {
    const [stats, setStats] = useState<Stats>({
        definitions: 0,
        instances: 0,
        loading: true,
        error: null
    })

    useEffect(() => {
        fetchStats()
    }, [])

    const fetchStats = async () => {
        try {
            const [defResponse, instResponse] = await Promise.all([
                processApi.getDefinitions({ page: 0, size: 1 }),
                instanceApi.getInstances({ page: 0, size: 1 })
            ])

            // Get counts from API
            const defCount = await fetch('/api/v1/process-definitions/count').then(r => r.json()).catch(() => ({ count: 0 }))
            const instCount = await fetch('/api/v1/process-instances/count').then(r => r.json()).catch(() => ({ count: 0 }))

            setStats({
                definitions: defCount.count || 0,
                instances: instCount.count || 0,
                loading: false,
                error: null
            })
        } catch (err) {
            console.error('Failed to fetch stats:', err)
            setStats(prev => ({ ...prev, loading: false, error: 'Failed to connect to backend' }))
        }
    }

    const statCards = [
        { label: 'Process Definitions', value: stats.definitions, icon: FileText, color: 'from-blue-600 to-blue-400', link: '/processes' },
        { label: 'Active Instances', value: stats.instances, icon: Activity, color: 'from-green-600 to-green-400', link: '/processes' },
        { label: 'Completed Today', value: 0, icon: CheckCircle, color: 'from-purple-600 to-purple-400', link: '#' },
        { label: 'Pending Tasks', value: 0, icon: Clock, color: 'from-orange-600 to-orange-400', link: '#' },
    ]

    const quickActions = [
        { label: 'New BPMN Process', icon: Plus, path: '/bpmn/new', color: 'bg-blue-600 hover:bg-blue-700' },
        { label: 'New DMN Decision', icon: Table2, path: '/dmn/new', color: 'bg-purple-600 hover:bg-purple-700' },
        { label: 'View Processes', icon: FileCode2, path: '/processes', color: 'bg-slate-600 hover:bg-slate-700' },
    ]

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex items-center gap-4 mb-8">
                <div className="p-3 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-xl">
                    <LayoutDashboard size={28} className="text-white" />
                </div>
                <div>
                    <h1 className="text-3xl font-bold text-white">Dashboard</h1>
                    <p className="text-slate-400">Welcome to the Workflow Modeler</p>
                </div>
            </div>

            {/* Connection Status */}
            {stats.error && (
                <div className="mb-6 p-4 bg-yellow-500/20 border border-yellow-500/50 rounded-lg text-yellow-400">
                    ⚠️ {stats.error} - Showing demo data
                </div>
            )}

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                {statCards.map((stat) => (
                    <Link
                        key={stat.label}
                        to={stat.link}
                        className="bg-slate-800 rounded-xl p-6 border border-slate-700 hover:border-slate-600 transition-all group"
                    >
                        <div className="flex items-center justify-between mb-4">
                            <div className={`p-3 rounded-lg bg-gradient-to-br ${stat.color} bg-opacity-20`}>
                                <stat.icon size={24} className="text-white" />
                            </div>
                        </div>
                        <p className="text-3xl font-bold text-white mb-1">
                            {stats.loading ? '...' : stat.value}
                        </p>
                        <p className="text-sm text-slate-400">{stat.label}</p>
                    </Link>
                ))}
            </div>

            {/* Quick Actions */}
            <div className="bg-slate-800 rounded-xl p-6 border border-slate-700 mb-8">
                <h2 className="text-lg font-semibold text-white mb-4">Quick Actions</h2>
                <div className="flex flex-wrap gap-4">
                    {quickActions.map((action) => (
                        <Link
                            key={action.path}
                            to={action.path}
                            className={`flex items-center gap-2 px-5 py-3 ${action.color} text-white rounded-lg transition-colors shadow-lg`}
                        >
                            <action.icon size={20} />
                            <span className="font-medium">{action.label}</span>
                        </Link>
                    ))}
                </div>
            </div>

            {/* API Status */}
            <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                <h2 className="text-lg font-semibold text-white mb-4">Backend Status</h2>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div className="flex items-center gap-3">
                        <div className={`w-3 h-3 rounded-full ${stats.error ? 'bg-red-500' : 'bg-green-500'}`}></div>
                        <span className="text-slate-300">Workflow Engine</span>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="w-3 h-3 rounded-full bg-gray-500"></div>
                        <span className="text-slate-300">Task Service</span>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="w-3 h-3 rounded-full bg-gray-500"></div>
                        <span className="text-slate-300">Form Service</span>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="w-3 h-3 rounded-full bg-gray-500"></div>
                        <span className="text-slate-300">Decision Engine</span>
                    </div>
                </div>
            </div>
        </div>
    )
}
