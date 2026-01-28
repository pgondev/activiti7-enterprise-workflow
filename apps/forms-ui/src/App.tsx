import React, { useEffect, useState } from 'react';
import axios from 'axios';

function App() {
    const [forms, setForms] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchForms();
    }, []);

    const fetchForms = async () => {
        try {
            const response = await axios.get('http://localhost:8082/api/v1/forms');
            setForms(response.data.content || []);
        } catch (error) {
            console.error('Error fetching forms:', error);
        } finally {
            setLoading(false);
        }
    };
    return (
        <div className="min-h-screen bg-gray-50 p-8">
            <div className="max-w-4xl mx-auto">
                <h1 className="text-3xl font-bold text-gray-900 mb-8">Public Forms</h1>

                {loading ? (
                    <div className="text-center py-12">Loading forms...</div>
                ) : (
                    <div className="bg-white rounded-lg shadow p-6">
                        {forms.length === 0 ? (
                            <div className="text-center py-12 text-gray-500">
                                <p className="mb-4">No published forms available.</p>
                                <p className="text-sm">Use the Modeler UI to create and publish forms.</p>
                            </div>
                        ) : (
                            <div className="grid gap-4">
                                {forms.map((form) => (
                                    <div key={form.id} className="border p-4 rounded hover:bg-gray-50 cursor-pointer">
                                        <h3 className="font-semibold">{form.name}</h3>
                                        <p className="text-sm text-gray-600">v{form.version}</p>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default App;
