import axios from 'axios'

const API_BASE = '/api/v1'

export const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    },
})

// Request interceptor for auth token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

// Process Definitions
export const processApi = {
    getDefinitions: (params?: { page?: number; size?: number }) =>
        api.get('/process-definitions', { params }),

    getDefinition: (id: string) =>
        api.get(`/process-definitions/${id}`),

    getDefinitionByKey: (key: string) =>
        api.get(`/process-definitions/key/${key}`),

    getDefinitionXml: (id: string) =>
        api.get(`/process-definitions/${id}/xml`),

    startProcess: (key: string, variables?: Record<string, unknown>) =>
        api.post(`/process-definitions/key/${key}/start`, { variables }),
}

// Deployments
export const deploymentApi = {
    getDeployments: (params?: { page?: number; size?: number }) =>
        api.get('/deployments', { params }),

    deploy: (name: string, bpmnXml: string) =>
        api.post('/deployments', { name, bpmnXml }),

    delete: (id: string) =>
        api.delete(`/deployments/${id}`),
}

// Process Instances
export const instanceApi = {
    getInstances: (params?: { page?: number; size?: number; status?: string }) =>
        api.get('/process-instances', { params }),

    getInstance: (id: string) =>
        api.get(`/process-instances/${id}`),

    getVariables: (id: string) =>
        api.get(`/process-instances/${id}/variables`),

    suspend: (id: string) =>
        api.post(`/process-instances/${id}/suspend`),

    activate: (id: string) =>
        api.post(`/process-instances/${id}/activate`),

    terminate: (id: string) =>
        api.delete(`/process-instances/${id}`),
}

export default api
