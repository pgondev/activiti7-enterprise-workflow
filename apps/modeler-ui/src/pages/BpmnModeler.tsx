import { useEffect, useRef, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Save, Download, Upload, Play, ZoomIn, ZoomOut, Maximize } from 'lucide-react'
import BpmnJS from 'bpmn-js/lib/Modeler'
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'

const defaultDiagram = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" 
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" 
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
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
        <dc:Bounds x="216" y="178" width="54" height="0" />
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Flow_2_di" bpmnElement="Flow_2">
        <dc:Bounds x="370" y="178" width="62" height="0" />
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`

export default function BpmnModeler() {
    const { id } = useParams()
    const containerRef = useRef<HTMLDivElement>(null)
    const modelerRef = useRef<BpmnJS | null>(null)
    const [processName, setProcessName] = useState('New Process')
    const [isDirty, setIsDirty] = useState(false)

    useEffect(() => {
        if (!containerRef.current) return

        const modeler = new BpmnJS({
            container: containerRef.current,
            keyboard: { bindTo: window },
        })

        modelerRef.current = modeler

        // Load diagram
        modeler.importXML(defaultDiagram).then(() => {
            const canvas = modeler.get('canvas') as any
            canvas.zoom('fit-viewport')
        }).catch(console.error)

        // Track changes
        modeler.on('commandStack.changed', () => {
            setIsDirty(true)
        })

        return () => {
            modeler.destroy()
        }
    }, [id])

    const handleSave = async () => {
        if (!modelerRef.current) return
        const { xml } = await modelerRef.current.saveXML({ format: true })
        console.log('Saved BPMN:', xml)
        // TODO: Call API to save
        setIsDirty(false)
    }

    const handleExport = async () => {
        if (!modelerRef.current) return
        const { xml } = await modelerRef.current.saveXML({ format: true })
        const blob = new Blob([xml || ''], { type: 'application/xml' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${processName.toLowerCase().replace(/\s+/g, '-')}.bpmn`
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
            {/* Toolbar */}
            <div className="h-14 bg-slate-800 border-b border-slate-700 flex items-center justify-between px-4">
                <div className="flex items-center gap-4">
                    <input
                        type="text"
                        value={processName}
                        onChange={(e) => setProcessName(e.target.value)}
                        className="bg-transparent text-white font-semibold text-lg focus:outline-none focus:border-b-2 focus:border-blue-500"
                    />
                    {isDirty && <span className="text-yellow-400 text-sm">• Unsaved changes</span>}
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
                        className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors shadow-lg shadow-blue-600/30"
                    >
                        <Save size={18} />
                        <span className="text-sm font-medium">Save</span>
                    </button>

                    <button
                        className="flex items-center gap-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors shadow-lg shadow-green-600/30"
                    >
                        <Play size={18} />
                        <span className="text-sm font-medium">Deploy</span>
                    </button>
                </div>
            </div>

            {/* Modeler Container */}
            <div ref={containerRef} className="flex-1 bpmn-container" />
        </div>
    )
}
