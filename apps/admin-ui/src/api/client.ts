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

// Process Instances API
export const instanceApi = {
    getInstances: (params?: { page?: number; size?: number; status?: string; processKey?: string }) =>
        api.get('/process-instances', { params }),

    getInstance: (id: string) =>
        api.get(`/process-instances/${id}`),

    getVariables: (id: string) =>
        api.get(`/process-instances/${id}/variables`),

    getHistory: (id: string) =>
        api.get(`/process-instances/${id}/history`),

    suspend: (id: string) =>
        api.post(`/process-instances/${id}/suspend`),

    activate: (id: string) =>
        api.post(`/process-instances/${id}/activate`),

    terminate: (id: string, reason?: string) =>
        api.delete(`/process-instances/${id}`, { data: { reason } }),

    setVariables: (id: string, variables: Record<string, unknown>) =>
        api.put(`/process-instances/${id}/variables`, { variables }),
}

// Deployments API
export const deploymentApi = {
    getDeployments: (params?: { page?: number; size?: number }) =>
        api.get('/deployments', { params }),

    getDeployment: (id: string) =>
        api.get(`/deployments/${id}`),

    getResources: (id: string) =>
        api.get(`/deployments/${id}/resources`),

    deploy: (name: string, files: File[]) => {
        const formData = new FormData()
        formData.append('name', name)
        files.forEach((file) => formData.append('files', file))
        return api.post('/deployments', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        })
    },

    delete: (id: string, cascade?: boolean) =>
        api.delete(`/deployments/${id}`, { params: { cascade } }),
}

// Process Definitions API
export const definitionApi = {
    getDefinitions: (params?: { page?: number; size?: number }) =>
        api.get('/process-definitions', { params }),

    getDefinition: (id: string) =>
        api.get(`/process-definitions/${id}`),

    startProcess: (key: string, variables?: Record<string, unknown>, businessKey?: string) =>
        api.post(`/process-definitions/key/${key}/start`, { variables, businessKey }),
}

// Users API (admin operations)
export const userApi = {
    getUsers: (params?: { page?: number; size?: number; search?: string }) =>
        api.get('/users', { params }),

    getUser: (id: string) =>
        api.get(`/users/${id}`),

    createUser: (user: { username: string; email: string; password: string; roles: string[] }) =>
        api.post('/users', user),

    updateUser: (id: string, user: Partial<{ email: string; roles: string[]; status: string }>) =>
        api.put(`/users/${id}`, user),

    deleteUser: (id: string) =>
        api.delete(`/users/${id}`),

    getRoles: () =>
        api.get('/roles'),
}

// Metrics API
export const metricsApi = {
    getDashboardStats: () =>
        api.get('/metrics/dashboard'),

    getProcessStats: (days?: number) =>
        api.get('/metrics/processes', { params: { days } }),

    getTaskStats: (days?: number) =>
        api.get('/metrics/tasks', { params: { days } }),

    getSystemHealth: () =>
        api.get('/actuator/health'),
}

export default api
