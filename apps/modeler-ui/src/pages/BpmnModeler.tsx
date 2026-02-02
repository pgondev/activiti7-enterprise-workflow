import { useEffect, useRef, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Save, Download, Upload, Play, ZoomIn, ZoomOut, Maximize, CheckCircle, AlertCircle } from 'lucide-react'
import BpmnJS from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
import '@bpmn-io/properties-panel/dist/assets/properties-panel.css'
import { BpmnPropertiesPanelModule, BpmnPropertiesProviderModule } from 'bpmn-js-properties-panel'
import { CamundaPlatformPropertiesProviderModule } from 'bpmn-js-properties-panel'
import CamundaBpmnModdle from 'camunda-bpmn-moddle/resources/camunda'
import FormKeyPropertiesProvider from '../bpmn/FormKeyPropertiesProvider'
import { deploymentApi, processApi } from '../api/client'

const defaultDiagram = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" 
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" 
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
  id="Definitions_1" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1" name="Start">
      <bpmn:outgoing>Flow_1</bpmn:outgoing>
    </bpmn:startEvent>
    <bpmn:task id="Task_1" name="Task 1">
      <bpmn:incoming>Flow_1</bpmn:incoming>
      <bpmn:outgoing>Flow_2</bpmn:outgoing>
    </bpmn:task>
    <bpmn:endEvent id="EndEvent_1" name="End">
      <bpmn:incoming>Flow_2</bpmn:incoming>
    </bpmn:endEvent>
    <bpmn:sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="Task_1" />
    <bpmn:sequenceFlow id="Flow_2" sourceRef="Task_1" targetRef="EndEvent_1" />
  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="StartEvent_1_di" bpmnElement="StartEvent_1">
        <dc:Bounds x="180" y="160" width="36" height="36" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="Task_1_di" bpmnElement="Task_1">
        <dc:Bounds x="270" y="138" width="100" height="80" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape id="EndEvent_1_di" bpmnElement="EndEvent_1">
        <dc:Bounds x="432" y="160" width="36" height="36" />
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge id="Flow_1_di" bpmnElement="Flow_1">
        <di:waypoint x="216" y="178" />
        <di:waypoint x="270" y="178" />
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Flow_2_di" bpmnElement="Flow_2">
        <di:waypoint x="370" y="178" />
        <di:waypoint x="432" y="178" />
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

export default function BpmnModeler() {
    const { id } = useParams()
    const navigate = useNavigate()
    const containerRef = useRef<HTMLDivElement>(null)
    const modelerRef = useRef<BpmnJS | null>(null)
    const [processName, setProcessName] = useState('New Process')
    const [processId, setProcessId] = useState('Process_1')
    const [isDirty, setIsDirty] = useState(false)
    const [status, setStatus] = useState<{ type: 'success' | 'error' | null; message: string }>({ type: null, message: '' })
    const [deploying, setDeploying] = useState(false)

    useEffect(() => {
        if (!containerRef.current) return

        // Create modular instance with Properties Panel
        const modeler = new BpmnJS({
            container: containerRef.current,
            propertiesPanel: {
                parent: '#js-properties-panel'
            },
            additionalModules: [
                BpmnPropertiesPanelModule,
                BpmnPropertiesProviderModule,
                CamundaPlatformPropertiesProviderModule, // Enables formKey and other Camunda properties
                FormKeyPropertiesProvider // Custom form selector dropdown
            ],
            moddleExtensions: {
                camunda: CamundaBpmnModdle
            },
            keyboard: { bindTo: window },
        } as any)

        modelerRef.current = modeler
        let isCancelled = false

        const loadDiagram = async () => {
            try {
                let xml = defaultDiagram
                let name = 'New Process'
                let elementId = 'Process_1'

                if (id) {
                    try {
                        const res = await processApi.getDefinitionXml(id)
                        const fetchedXml = res.data
                        if (typeof fetchedXml === 'string' && fetchedXml.trim().length > 0) {
                            xml = fetchedXml
                            // Fetch name
                            try {
                                const details = await processApi.getDefinition(id)
                                name = details.data.name || details.data.key
                                elementId = details.data.key
                            } catch (e) {
                                console.warn('Failed to fetch process name', e)
                            }
                        } else {
                            throw new Error('Empty XML received')
                        }
                    } catch (e) {
                        console.error('Failed to fetch definition', e)
                        setStatus({ type: 'error', message: 'Failed to load process definition' })
                        return // Stop if fetch failed
                    }
                }

                if (isCancelled) return

                await modeler.importXML(xml)

                if (isCancelled) return

                const canvas = modeler.get('canvas') as any
                canvas.zoom('fit-viewport')
                if (id) {
                    setProcessName(name)
                    setProcessId(elementId)
                }

            } catch (err) {
                if (!isCancelled) {
                    console.error('BPMN Import Error:', err)
                    setStatus({ type: 'error', message: 'Failed to render BPMN diagram' })
                }
            }
        }

        loadDiagram()

        // Track changes
        modeler.on('commandStack.changed', () => {
            setIsDirty(true)
        })

        return () => {
            isCancelled = true
            modeler.destroy()
        }
    }, [id])

    const handleNameChange = (newName: string) => {
        setProcessName(newName)
        // If creating a new process (no ID param), auto-generate key
        if (!id) {
            const newKey = newName.toLowerCase()
                .replace(/[^a-z0-9]+/g, '_')
                .replace(/^_+|_+$/g, '')
            if (newKey) setProcessId(newKey)
        }
    }

    const handleSave = async () => {
        if (!modelerRef.current) return
        let { xml } = await modelerRef.current.saveXML({ format: true })

        // Patch ID
        if (xml && processId) {
            const match = xml.match(/<bpmn:process id="([^"]+)"/)
            const oldId = match ? match[1] : null
            if (oldId && oldId !== processId) {
                // Safe string replacement for unique ID
                xml = xml.split(oldId).join(processId)
            }
        }

        console.log('Saved BPMN:', xml)
        const blob = new Blob([xml || ''], { type: 'application/xml' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${processName.toLowerCase().replace(/\s+/g, '-')}.bpmn20.xml`
        a.click()
        URL.revokeObjectURL(url)
        setIsDirty(false)
        setStatus({ type: 'success', message: 'File downloaded' })
        setTimeout(() => setStatus({ type: null, message: '' }), 3000)
    }

    const handleDeploy = async () => {
        if (!modelerRef.current) return

        setDeploying(true)
        setStatus({ type: null, message: '' })

        try {
            let { xml } = await modelerRef.current.saveXML({ format: true })

            // Patch ID logic
            if (xml && processId) {
                const match = xml.match(/<bpmn:process id="([^"]+)"/)
                const oldId = match ? match[1] : null
                if (oldId && oldId !== processId) {
                    // Safe string replacement for unique ID
                    xml = xml.split(oldId).join(processId)
                }
            }

            const response = await deploymentApi.deploy(processName, xml || '')

            setIsDirty(false)
            setStatus({ type: 'success', message: `Deployed successfully! ID: ${response.data.id}` })

            // Redirect to process list after 2 seconds
            setTimeout(() => {
                navigate('/processes')
            }, 2000)
        } catch (err: any) {
            console.error('Deployment failed:', err)
            setStatus({
                type: 'error',
                message: err.response?.data?.message || err.message || 'Deployment failed. Is the backend running?'
            })
        } finally {
            setDeploying(false)
        }
    }

    const handleExport = async () => {
        if (!modelerRef.current) return
        const { xml } = await modelerRef.current.saveXML({ format: true })
        const blob = new Blob([xml || ''], { type: 'application/xml' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${processName.toLowerCase().replace(/\s+/g, '-')}.bpmn20.xml`
        a.click()
        URL.revokeObjectURL(url)
    }

    const handleImport = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0]
        if (!file || !modelerRef.current) return

        const reader = new FileReader()
        reader.onload = async (e) => {
            const xml = e.target?.result as string
            await modelerRef.current?.importXML(xml)
            const canvas = modelerRef.current?.get('canvas') as any
            canvas?.zoom('fit-viewport')
            setProcessName(file.name.replace(/\.bpmn$|\.xml$/, ''))
        }
        reader.readAsText(file)
    }

    const handleZoomIn = () => {
        const canvas = modelerRef.current?.get('canvas') as any
        canvas?.zoom(canvas.zoom() * 1.2)
    }

    const handleZoomOut = () => {
        const canvas = modelerRef.current?.get('canvas') as any
        canvas?.zoom(canvas.zoom() / 1.2)
    }

    const handleFitViewport = () => {
        const canvas = modelerRef.current?.get('canvas') as any
        canvas?.zoom('fit-viewport')
    }

    return (
        <div className="h-full flex flex-col">
            {/* Status Banner */}
            {status.type && (
                <div className={`px-4 py-2 flex items-center gap-2 ${status.type === 'success' ? 'bg-green-600' : 'bg-red-600'
                    } text-white`}>
                    {status.type === 'success' ? <CheckCircle size={18} /> : <AlertCircle size={18} />}
                    {status.message}
                </div>
            )}

            {/* Toolbar */}
            <div className="h-14 bg-slate-800 border-b border-slate-700 flex items-center justify-between px-4">
                <div className="flex items-center gap-4">
                    <div className="flex flex-col">
                        <label className="text-[10px] text-slate-400 uppercase font-bold tracking-wider">Name</label>
                        <input
                            type="text"
                            value={processName}
                            onChange={(e) => handleNameChange(e.target.value)}
                            className="bg-transparent text-white font-semibold text-sm focus:outline-none focus:border-b focus:border-blue-500 w-64"
                            aria-label="Process name"
                            placeholder="Process Name"
                        />
                    </div>
                    {isDirty && <span className="text-yellow-400 text-xs ml-2">• Unsaved changes</span>}
                </div>

                <div className="flex items-center gap-2">
                    {/* Zoom Controls */}
                    <div className="flex items-center gap-1 mr-4 bg-slate-700 rounded-lg p-1">
                        <button onClick={handleZoomOut} className="p-2 hover:bg-slate-600 rounded" title="Zoom Out">
                            <ZoomOut size={18} className="text-slate-300" />
                        </button>
                        <button onClick={handleFitViewport} className="p-2 hover:bg-slate-600 rounded" title="Fit">
                            <Maximize size={18} className="text-slate-300" />
                        </button>
                        <button onClick={handleZoomIn} className="p-2 hover:bg-slate-600 rounded" title="Zoom In">
                            <ZoomIn size={18} className="text-slate-300" />
                        </button>
                    </div>

                    {/* File Operations */}
                    <label className="flex items-center gap-2 px-3 py-2 text-slate-300 hover:bg-slate-700 rounded-lg cursor-pointer transition-colors">
                        <Upload size={18} />
                        <span className="text-sm">Import</span>
                        <input type="file" accept=".bpmn,.xml" onChange={handleImport} className="hidden" />
                    </label>

                    <button
                        onClick={handleExport}
                        className="flex items-center gap-2 px-3 py-2 text-slate-300 hover:bg-slate-700 rounded-lg transition-colors"
                    >
                        <Download size={18} />
                        <span className="text-sm">Export</span>
                    </button>

                    <button
                        onClick={handleSave}
                        className="flex items-center gap-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors shadow-lg shadow-indigo-600/30"
                        title="Download XML file"
                    >
                        <Save size={18} />
                        <span className="text-sm font-medium">Download</span>
                    </button>

                    <button
                        onClick={handleDeploy}
                        disabled={deploying}
                        className={`flex items-center gap-2 px-4 py-2 text-white rounded-lg transition-colors shadow-lg ${deploying
                            ? 'bg-gray-600 cursor-not-allowed'
                            : 'bg-green-600 hover:bg-green-700 shadow-green-600/30'
                            }`}
                    >
                        <Play size={18} className={deploying ? 'animate-pulse' : ''} />
                        <span className="text-sm font-medium">{deploying ? 'Deploying...' : 'Deploy'}</span>
                    </button>
                </div>
            </div>

            {/* Modeler Container */}
            <div className="flex-1 flex overflow-hidden">
                <div ref={containerRef} className="flex-1 bpmn-container" />
                <div id="js-properties-panel" className="w-[300px] border-l border-slate-700 bg-slate-50 overflow-y-auto" />
            </div>
        </div>
    )
}
