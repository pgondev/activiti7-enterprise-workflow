import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Plus, Search, FileText, Calendar, User } from 'lucide-react'

interface FormDefinition {
    id: string
    key: string
    name: string
    version: number
    updatedAt: string
    createdBy: string
    description?: string
}

export default function FormList() {
    const [forms, setForms] = useState<FormDefinition[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [searchTerm, setSearchTerm] = useState('')

    useEffect(() => {
        fetchForms()
    }, [])

    const fetchForms = async () => {
        try {
            const response = await fetch('/api/v1/forms')
            if (!response.ok) throw new Error('Failed to fetch forms')
            const data = await response.json()
            // Support both paged response (content array) and list response
            const formList = data.content || data
            setForms(formList)
        } catch (err) {
            setError('Failed to load forms. Is the backend running?')
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    const deleteForm = async (id: string) => {
        if (!confirm('Are you sure you want to delete this form?')) return

        try {
            const response = await fetch(`/api/v1/forms/${id}`, {
                method: 'DELETE'
            })
            if (!response.ok) throw new Error('Failed to delete form')
            fetchForms() // Reload list
        } catch (error) {
            console.error('Error deleting form:', error)
            alert('Failed to delete form')
        }
    }

    const filteredForms = forms.filter(form =>
        form.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        form.key.toLowerCase().includes(searchTerm.toLowerCase())
    )

    return (
        <div className="p-8 animate-fade-in">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-800">Forms</h1>
                    <p className="text-slate-500">Manage your form definitions</p>
                </div>
                <Link
                    to="/modeler/form"
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                    <Plus size={20} />
                    <span>Create Form</span>
                </Link>
            </div>

            {/* Search */}
            <div className="mb-6">
                <div className="relative max-w-md">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
                    <input
                        type="text"
                        placeholder="Search forms..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
                    />
                </div>
            </div>

            {error && (
                <div className="p-4 mb-6 bg-red-50 text-red-600 rounded-lg border border-red-200">
                    {error}
                </div>
            )}

            {loading ? (
                <div className="text-center py-12 text-gray-500">Loading forms...</div>
            ) : filteredForms.length === 0 ? (
                <div className="text-center py-12 bg-gray-50 rounded-xl border-dashed border-2 border-gray-200">
                    <FileText className="mx-auto text-gray-400 mb-4" size={48} />
                    <h3 className="text-lg font-medium text-gray-900">No forms found</h3>
                    <p className="text-gray-500 mb-4">Get started by creating your first form</p>
                    <Link
                        to="/modeler/form"
                        className="text-blue-600 hover:text-blue-700 font-medium"
                    >
                        Create a form &rarr;
                    </Link>
                </div>
            ) : (
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 border-b border-gray-200">
                            <tr>
                                <th className="px-6 py-4 font-semibold text-gray-700">Form Name</th>
                                <th className="px-6 py-4 font-semibold text-gray-700">Key</th>
                                <th className="px-6 py-4 font-semibold text-gray-700">Version</th>
                                <th className="px-6 py-4 font-semibold text-gray-700">Last Updated</th>
                                <th className="px-6 py-4 font-semibold text-gray-700">Created By</th>
                                <th className="px-6 py-4 font-semibold text-gray-700">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {filteredForms.map((form) => (
                                <tr key={form.id} className="hover:bg-gray-50 transition-colors">
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            <div className="p-2 bg-blue-100 text-blue-600 rounded-lg">
                                                <FileText size={18} />
                                            </div>
                                            <div>
                                                <div className="font-medium text-gray-900">{form.name}</div>
                                                {form.description && <div className="text-xs text-gray-500">{form.description}</div>}
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 font-mono text-sm text-gray-600">{form.key}</td>
                                    <td className="px-6 py-4">
                                        <span className="px-2 py-1 bg-gray-100 text-gray-600 text-xs font-medium rounded-full">
                                            v{form.version}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-600">
                                        <div className="flex items-center gap-2">
                                            <Calendar size={14} className="text-gray-400" />
                                            {form.updatedAt ? new Date(form.updatedAt).toLocaleDateString() : '-'}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-600">
                                        <div className="flex items-center gap-2">
                                            <User size={14} className="text-gray-400" />
                                            {form.createdBy || 'System'}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            <Link
                                                to={`/modeler/form/${form.id}`}
                                                className="text-blue-600 hover:text-blue-800 font-medium text-sm"
                                            >
                                                Edit
                                            </Link>
                                            <button
                                                onClick={() => deleteForm(form.id)}
                                                className="text-red-600 hover:text-red-800 font-medium text-sm"
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
