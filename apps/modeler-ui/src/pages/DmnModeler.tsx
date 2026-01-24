import { useEffect, useRef, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Save, Download, Upload, Play } from 'lucide-react'
import DmnJS from 'dmn-js/lib/Modeler'
import 'dmn-js/dist/assets/diagram-js.css'
import 'dmn-js/dist/assets/dmn-js-shared.css'
import 'dmn-js/dist/assets/dmn-js-drd.css'
import 'dmn-js/dist/assets/dmn-js-decision-table.css'
import 'dmn-js/dist/assets/dmn-font/css/dmn.css'

const defaultDmn = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="https://www.omg.org/spec/DMN/20191111/MODEL/" 
  id="definitions" name="Decision" namespace="http://camunda.org/schema/1.0/dmn">
  <decision id="decision_1" name="Decision 1">
    <decisionTable id="decisionTable_1">
      <input id="input_1" label="Input">
        <inputExpression id="inputExpression_1" typeRef="string">
          <text>input</text>
        </inputExpression>
      </input>
      <output id="output_1" label="Output" typeRef="string" />
      <rule id="rule_1">
        <inputEntry id="inputEntry_1">
          <text>"value"</text>
        </inputEntry>
        <outputEntry id="outputEntry_1">
          <text>"result"</text>
        </outputEntry>
      </rule>
    </decisionTable>
  </decision>
</definitions>`

export default function DmnModeler() {
    const { id } = useParams()
    const containerRef = useRef<HTMLDivElement>(null)
    const modelerRef = useRef<DmnJS | null>(null)
    const [decisionName, setDecisionName] = useState('New Decision Table')
    const [isDirty, setIsDirty] = useState(false)

    useEffect(() => {
        if (!containerRef.current) return

        const modeler = new DmnJS({
            container: containerRef.current,
        })

        modelerRef.current = modeler

        modeler.importXML(defaultDmn).catch(console.error)

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
        console.log('Saved DMN:', xml)
        setIsDirty(false)
    }

    const handleExport = async () => {
        if (!modelerRef.current) return
        const { xml } = await modelerRef.current.saveXML({ format: true })
        const blob = new Blob([xml || ''], { type: 'application/xml' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${decisionName.toLowerCase().replace(/\s+/g, '-')}.dmn`
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
        }
        reader.readAsText(file)
    }

    return (
        <div className="h-full flex flex-col">
            {/* Toolbar */}
            <div className="h-14 bg-slate-800 border-b border-slate-700 flex items-center justify-between px-4">
                <div className="flex items-center gap-4">
                    <input
                        type="text"
                        value={decisionName}
                        onChange={(e) => setDecisionName(e.target.value)}
                        className="bg-transparent text-white font-semibold text-lg focus:outline-none focus:border-b-2 focus:border-indigo-500"
                    />
                    {isDirty && <span className="text-yellow-400 text-sm">• Unsaved changes</span>}
                </div>

                <div className="flex items-center gap-2">
                    <label className="flex items-center gap-2 px-3 py-2 text-slate-300 hover:bg-slate-700 rounded-lg cursor-pointer transition-colors">
                        <Upload size={18} />
                        <span className="text-sm">Import</span>
                        <input type="file" accept=".dmn,.xml" onChange={handleImport} className="hidden" />
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
            <div ref={containerRef} className="flex-1 bg-white" />
        </div>
    )
}
