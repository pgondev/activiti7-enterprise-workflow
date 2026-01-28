import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ArrowLeft, Clock, User, Activity, Play, Pause, XCircle } from 'lucide-react'
import { instanceApi, processApi } from '../api/client'
import { format } from 'date-fns'
import BpmnViewer from 'bpmn-js/lib/NavigatedViewer'

interface ProcessInstance {
    id: string
    name: string
    processDefinitionId: string
    processDefinitionKey: string
    startTime: string
    endTime: string | null
    startUserId: string | null
    status: 'RUNNING' | 'COMPLETED' | 'SUSPENDED'
    variables?: Record<string, any>
}

export default function ProcessInstanceDetails() {
    const { id } = useParams<{ id: string }>()
    const [instance, setInstance] = useState<ProcessInstance | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [xml, setXml] = useState<string | null>(null)

    useEffect(() => {
        if (id) fetchInstance(id)
    }, [id])

    useEffect(() => {
        if (xml && instance) {
            const viewer = new BpmnViewer({
                container: '#canvas',
                height: 400
            })

            viewer.importXML(xml).then(() => {
                const canvas = viewer.get('canvas') as any
                canvas.zoom('fit-viewport')

                // Highlight active path if running?
                // For now, simple viewer.
            }).catch(e => console.error('BPMN render error', e))
        }
    }, [xml, instance])

    const fetchInstance = async (instanceId: string) => {
        try {
            const res = await instanceApi.getInstance(instanceId)
            const varsRes = await instanceApi.getVariables(instanceId)

            const i = res.data
            setInstance({
                ...i,
                status: i.endTime ? 'COMPLETED' : (i.suspended ? 'SUSPENDED' : 'RUNNING'),
                variables: varsRes.data
            })

            // Get XML for diagram
            const xmlRes = await processApi.getDefinitionXml(i.processDefinitionId)
            setXml(xmlRes.data)

        } catch (err) {
            console.error(err)
            setError('Failed to load instance details')
        } finally {
            setLoading(false)
        }
    }

    const handleAction = async (action: 'suspend' | 'activate' | 'terminate') => {
        if (!instance || !id) return
        try {
            if (action === 'suspend') await instanceApi.suspend(id)
            if (action === 'activate') await instanceApi.activate(id)
            if (action === 'terminate') await instanceApi.terminate(id)
            fetchInstance(id)
        } catch (err) {
            alert(`Failed to ${action} instance`)
        }
    }

    if (loading) return <div className="p-8 text-slate-400">Loading...</div>
    if (error || !instance) return <div className="p-8 text-red-400">{error || 'Instance not found'}</div>

    return (
        <div className="p-8 animate-fade-in h-screen flex flex-col">
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-4">
                    <Link to="/modeler/instances" className="p-2 hover:bg-slate-800 rounded-lg text-slate-400 transition-colors">
                        <ArrowLeft size={20} />
                    </Link>
                    <div>
                        <h1 className="text-2xl font-bold text-white mb-1">
                            Instance: {instance.id.substring(0, 8)}...
                        </h1>
                        <p className="text-slate-400 flex items-center gap-2">
                            <Activity size={16} />
                            {instance.processDefinitionKey}
                        </p>
                    </div>
                </div>

                <div className="flex gap-2">
                    {instance.status === 'RUNNING' && (
                        <>
                            <button onClick={() => handleAction('suspend')} className="flex items-center gap-2 px-4 py-2 bg-yellow-600/20 text-yellow-500 hover:bg-yellow-600/30 rounded-lg transition-colors">
                                <Pause size={18} /> Suspend
                            </button>
                            <button onClick={() => handleAction('terminate')} className="flex items-center gap-2 px-4 py-2 bg-red-600/20 text-red-500 hover:bg-red-600/30 rounded-lg transition-colors">
                                <XCircle size={18} /> Terminate
                            </button>
                        </>
                    )}
                    {instance.status === 'SUSPENDED' && (
                        <button onClick={() => handleAction('activate')} className="flex items-center gap-2 px-4 py-2 bg-green-600/20 text-green-500 hover:bg-green-600/30 rounded-lg transition-colors">
                            <Play size={18} /> Activate
                        </button>
                    )}
                </div>
            </div>

            <div className="grid grid-cols-3 gap-6 flex-1 min-h-0">
                {/* Main Content - Diagram & Details */}
                <div className="col-span-2 flex flex-col gap-6">
                    {/* Diagram */}
                    <div className="bg-white rounded-xl h-96 overflow-hidden border border-slate-700 relative">
                        <div id="canvas" className="w-full h-full"></div>
                        <div className="absolute top-4 right-4 bg-white/90 px-3 py-1 rounded text-xs font-mono border">
                            {instance.processDefinitionKey}
                        </div>
                    </div>

                    {/* Timeline/History (Placeholder) */}
                    <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                        <h3 className="text-lg font-semibold text-white mb-4">Activity History</h3>
                        <p className="text-slate-500 italic">Audit history coming soon...</p>
                    </div>
                </div>

                {/* Sidebar - Meta & Variables */}
                <div className="space-y-6">
                    <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                        <h3 className="text-lg font-semibold text-white mb-4">Details</h3>
                        <div className="space-y-4">
                            <MetaItem label="Status" value={<StatusBadge status={instance.status} />} />
                            <MetaItem label="Started" value={format(new Date(instance.startTime), 'MMM d, HH:mm')} />
                            <MetaItem label="Ended" value={instance.endTime ? format(new Date(instance.endTime), 'MMM d, HH:mm') : '-'} />
                            <MetaItem label="Initiator" value={instance.startUserId || 'System'} />
                            <MetaItem label="Definition ID" value={instance.processDefinitionId} />
                        </div>
                    </div>

                    <div className="bg-slate-800 rounded-xl p-6 border border-slate-700">
                        <h3 className="text-lg font-semibold text-white mb-4">Variables</h3>
                        {instance.variables && Object.keys(instance.variables).length > 0 ? (
                            <div className="space-y-3">
                                {Object.entries(instance.variables).map(([key, val]) => (
                                    <div key={key} className="bg-slate-900/50 p-3 rounded-lg">
                                        <div className="text-xs text-slate-500 mb-1">{key}</div>
                                        <div className="text-sm text-slate-200 font-mono break-all">
                                            {JSON.stringify(val)}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="text-slate-500 text-sm">No variables found</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

function MetaItem({ label, value }: { label: string, value: React.ReactNode }) {
    return (
        <div>
            <div className="text-xs text-slate-500 mb-1">{label}</div>
            <div className="text-sm text-slate-200">{value}</div>
        </div>
    )
}

function StatusBadge({ status }: { status: ProcessInstance['status'] }) {
    const styles = {
        RUNNING: 'text-green-400',
        COMPLETED: 'text-slate-400',
        SUSPENDED: 'text-yellow-400',
    }
    return <span className={`font-medium ${styles[status]}`}>{status}</span>
}
