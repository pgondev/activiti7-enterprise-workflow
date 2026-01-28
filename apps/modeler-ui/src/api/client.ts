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
        api.post(`/process-instances/key/${key}/start`, variables || {}),
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

// Decisions
export const decisionApi = {
    getDecisions: (params?: { page?: number; size?: number }) =>
        api.get('/decisions', { params }),

    getDecision: (id: string) =>
        api.get(`/decisions/${id}`),

    getDecisionXml: (id: string) =>
        api.get(`/decisions/${id}/xml`),

    deploy: (name: string, dmnXml: string) =>
        api.post('/decisions/deploy', { name, dmnXml }),

    delete: (deploymentId: string) =>
        api.delete(`/decisions/deployments/${deploymentId}`),
}

// Health
export const healthApi = {
    // We use list endpoints as health checks since actuator might be restricted or on different path
    checkWorkflow: () => api.get('/process-definitions?page=0&size=1'),
    checkTasks: () => api.get('/tasks?page=0&size=1'),
    checkForms: () => api.get('/forms?page=0&size=1'),
    checkDecisions: () => api.get('/decisions?page=0&size=1'),
}

// Tasks
export const taskApi = {
    getTasks: (params?: { page?: number; size?: number; assignee?: string }) =>
        api.get('/tasks', { params }),

    getTask: (id: string) =>
        api.get(`/tasks/${id}`),

    claim: (id: string) =>
        api.post(`/tasks/${id}/claim`),

    complete: (id: string, variables?: Record<string, unknown>) =>
        api.post(`/tasks/${id}/complete`, variables || {}),
}

// Forms
export const formApi = {
    getForms: (params?: { page?: number; size?: number }) =>
        api.get('/forms', { params }),

    getForm: (id: string) =>
        api.get(`/forms/${id}`),
}

export default api
