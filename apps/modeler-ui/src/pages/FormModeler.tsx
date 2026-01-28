import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { FormBuilder } from '@formio/react'
import 'formiojs/dist/formio.full.css'

export default function FormModeler() {
    const [form, setForm] = useState({ display: 'form' })
    const [formName, setFormName] = useState('New Form')
    const [formKey, setFormKey] = useState('new-form')
    const [isDeploying, setIsDeploying] = useState(false)
    const { id } = useParams()

    useEffect(() => {
        if (id) {
            fetchForm(id)
        }
    }, [id])

    const fetchForm = async (formId: string) => {
        try {
            const response = await fetch(`/api/v1/forms/${formId}`)
            if (!response.ok) throw new Error('Failed to fetch form')
            const data = await response.json()
            setFormName(data.name)
            setFormKey(data.key)
            if (data.schema) {
                // If schema is string (older versions), parse it
                const schemaObj = typeof data.schema === 'string' ? JSON.parse(data.schema) : data.schema
                setForm(schemaObj)
            }
        } catch (error) {
            console.error('Error fetching form:', error)
            alert('Failed to load form definition')
        }
    }

    const deployForm = async () => {
        setIsDeploying(true)
        try {
            // If we have an ID, we could be updating, but for now we create new version with same key
            // Ideally backend handles versioning by Key.
            const response = await fetch('/api/v1/forms', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    key: formKey,
                    name: formName,
                    description: 'Deployed from Modeler UI',
                    category: 'workflow',
                    schema: form
                }),
            })

            if (!response.ok) {
                const text = await response.text()
                try {
                    const errorData = JSON.parse(text)
                    throw new Error(errorData.message || `Server Error: ${response.status}`)
                } catch (e) {
                    throw new Error(`Deployment failed (${response.status}): ${text.substring(0, 100)}`)
                }
            }

            const data = await response.json()
            alert(`✅ Form Deployed Successfully!\nID: ${data.id}\nVersion: ${data.version}`)
        } catch (error: any) {
            alert(`❌ Error: ${error.message}`)
            console.error(error)
        } finally {
            setIsDeploying(false)
        }
    }

    const saveForm = (schema: any) => {
        setForm(schema)
        console.log('Form Schema:', schema)
        // Ensure form.components is an array before mapping
        const components = Array.isArray(schema.components) ? schema.components : []
        alert(`Form Saved! (${components.length} components)`)
    }

    return (
        <div className="h-full flex flex-col bg-white">
            <div className="border-b border-gray-200 px-6 py-4 flex items-center justify-between">
                <div className="flex gap-4 items-end">
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800">Form Designer</h2>
                        <p className="text-sm text-gray-500">Design dynamic forms using drag-and-drop</p>
                    </div>
                    <div className="flex flex-col gap-1 ml-4">
                        <label className="text-xs text-gray-500 font-medium">Form Name</label>
                        <input
                            type="text"
                            value={formName}
                            onChange={(e) => {
                                setFormName(e.target.value)
                                setFormKey(e.target.value.toLowerCase().replace(/[^a-z0-9]/g, '-'))
                            }}
                            className="border border-gray-300 rounded px-2 py-1 text-sm w-48 focus:outline-none focus:border-blue-500"
                        />
                    </div>
                    <div className="flex flex-col gap-1">
                        <label className="text-xs text-gray-500 font-medium">Key</label>
                        <input
                            type="text"
                            value={formKey}
                            onChange={(e) => setFormKey(e.target.value)}
                            className="border border-gray-300 rounded px-2 py-1 text-sm w-40 bg-gray-50 text-gray-600 font-mono"
                        />
                    </div>
                </div>
                <div className="flex gap-2">
                    <button
                        onClick={deployForm}
                        disabled={isDeploying}
                        className={`px-4 py-2 text-white rounded-lg transition-colors flex items-center gap-2 ${isDeploying ? 'bg-blue-400 cursor-wait' : 'bg-blue-600 hover:bg-blue-700'
                            }`}
                    >
                        {isDeploying ? 'Deploying...' : 'Deploy Form'}
                    </button>
                    <button
                        onClick={() => saveForm(form)}
                        className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                        Save Local
                    </button>
                </div>
            </div>

            <div className="flex-1 overflow-auto p-6">
                <div className="bg-gray-50 rounded-lg shadow-sm border border-gray-200 p-4 min-h-[600px]">
                    <FormBuilder
                        form={form}
                        onChange={(schema: any) => setForm(schema)}
                        options={{}}
                    />
                </div>
            </div>
        </div>
    )
}
