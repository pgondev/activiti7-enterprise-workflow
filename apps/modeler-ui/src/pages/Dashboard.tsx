import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { LayoutDashboard, FileCode2, Table2, Plus, Activity, CheckCircle, Clock, FileText, Settings, List } from 'lucide-react'
import { processApi, instanceApi, healthApi } from '../api/client'

interface ServiceStatus {
    workflow: boolean
    task: boolean
    form: boolean
    decision: boolean
}

interface Stats {
    definitions: number
    instances: number
    loading: boolean
    error: string | null
    services: ServiceStatus
}

export default function Dashboard() {
    const [stats, setStats] = useState<Stats>({
        definitions: 0,
        instances: 0,
        loading: true,
        error: null,
        services: {
            workflow: false,
            task: false,
            form: false,
            decision: false
        }
    })

    useEffect(() => {
        fetchStats()
    }, [])

    const fetchStats = async () => {
        // Parallel checks
        const results = await Promise.allSettled([
            healthApi.checkWorkflow(),
            healthApi.checkTasks(),
            healthApi.checkForms(),
            healthApi.checkDecisions(),
            processApi.getDefinitions({ page: 0, size: 1 }),
            instanceApi.getInstances({ page: 0, size: 1 })
        ])

        const [wfRes, taskRes, formRes, dmnRes, defRes, instRes] = results

        const services = {
            workflow: wfRes.status === 'fulfilled',
            task: taskRes.status === 'fulfilled',
            form: formRes.status === 'fulfilled',
            decision: dmnRes.status === 'fulfilled'
        }

        // Get counts safely
        let definitions = 0
        let instances = 0

        if (defRes.status === 'fulfilled' && defRes.value.data) {
            // If data is array (search result), use total? 
            // Currently backend returns list. We can use header or just length of page 
            // But for total count we ideally call /count endpoint
            definitions = defRes.value.data.length // This is just page size. 
            // Let's try to fetch counts if workflow is up
        }

        if (services.workflow) {
            try {
                const defCount = await processApi.getDefinitions().then(r => r.headers['x-total-count'] || r.data.length).catch(() => 0)
                // Or separate /count endpoints if they exist. 
                // Previous code used /api/v1/process-definitions/count. Let's keep that pattern if valid.
                const dCount = await fetch('/api/v1/process-definitions/count').then(r => r.json()).catch(() => ({ count: 0 }))
                const iCount = await fetch('/api/v1/process-instances/count').then(r => r.json()).catch(() => ({ count: 0 }))
                definitions = dCount.count
                instances = iCount.count
            } catch (e) { console.warn('Count fetch failed', e) }
        }

        setStats({
            definitions,
            instances,
            loading: false,
            error: services.workflow ? null : 'Workflow Engine unreachable',
            services
        })
    }

    const statCards = [
        { label: 'Process Definitions', value: stats.definitions, icon: FileText, color: 'from-blue-600 to-blue-400', link: '/processes' },
        { label: 'Active Instances', value: stats.instances, icon: Activity, color: 'from-green-600 to-green-400', link: '/modeler/instances' },
        { label: 'Completed Today', value: 0, icon: CheckCircle, color: 'from-purple-600 to-purple-400', link: '#' },
        { label: 'Pending Tasks', value: 0, icon: Clock, color: 'from-orange-600 to-orange-400', link: '/modeler/tasks' },
    ]

    const quickActions = [
        { label: 'New BPMN Process', icon: Plus, path: '/modeler/bpmn', color: 'bg-blue-600 hover:bg-blue-700' },
        { label: 'New DMN Decision', icon: Table2, path: '/modeler/dmn', color: 'bg-purple-600 hover:bg-purple-700' },
        { label: 'View Processes', icon: FileCode2, path: '/processes', color: 'bg-slate-600 hover:bg-slate-700' },
        { label: 'View Tasks', icon: List, path: '/modeler/tasks', color: 'bg-emerald-600 hover:bg-emerald-700' },
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
                <div className="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-400 flex items-center gap-2">
                    <Activity size={20} />
                    {stats.error}. Check backend services.
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
                    <StatusDot label="Workflow Engine" active={stats.services.workflow} />
                    <StatusDot label="Task Service" active={stats.services.task} />
                    <StatusDot label="Form Service" active={stats.services.form} />
                    <StatusDot label="Decision Engine" active={stats.services.decision} />
                </div>
            </div>
        </div>
    )
}

function StatusDot({ label, active }: { label: string; active: boolean }) {
    return (
        <div className="flex items-center gap-3">
            <div className={`w-3 h-3 rounded-full ${active ? 'bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.6)]' : 'bg-red-500/50'}`}></div>
            <span className={`${active ? 'text-slate-200' : 'text-slate-500'}`}>{label}</span>
        </div>
    )
}
