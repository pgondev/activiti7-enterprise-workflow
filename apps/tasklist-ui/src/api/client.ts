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

// Tasks API
export const taskApi = {
    // Get tasks (inbox - unassigned)
    getInboxTasks: (params?: { page?: number; size?: number; priority?: string }) =>
        api.get('/tasks', { params: { ...params, assignee: null } }),

    // Get my tasks (assigned to current user)
    getMyTasks: (params?: { page?: number; size?: number }) =>
        api.get('/tasks/assignee/me', { params }),

    // Get task by ID
    getTask: (taskId: string) =>
        api.get(`/tasks/${taskId}`),

    // Get task variables
    getTaskVariables: (taskId: string) =>
        api.get(`/tasks/${taskId}/variables`),

    // Claim a task
    claimTask: (taskId: string) =>
        api.post(`/tasks/${taskId}/claim`),

    // Unclaim a task
    unclaimTask: (taskId: string) =>
        api.post(`/tasks/${taskId}/unclaim`),

    // Complete a task
    completeTask: (taskId: string, variables?: Record<string, unknown>) =>
        api.post(`/tasks/${taskId}/complete`, { variables }),

    // Add comment to task
    addComment: (taskId: string, message: string) =>
        api.post(`/tasks/${taskId}/comments`, { message }),

    // Get task comments
    getComments: (taskId: string) =>
        api.get(`/tasks/${taskId}/comments`),

    // Upload attachment
    uploadAttachment: (taskId: string, file: File) => {
        const formData = new FormData()
        formData.append('file', file)
        return api.post(`/tasks/${taskId}/attachments`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        })
    },

    // Get attachments
    getAttachments: (taskId: string) =>
        api.get(`/tasks/${taskId}/attachments`),
}

// Forms API
export const formApi = {
    // Get form for task
    getTaskForm: (taskId: string) =>
        api.get(`/forms/task/${taskId}`),

    // Submit form
    submitForm: (formId: string, data: Record<string, unknown>) =>
        api.post(`/forms/${formId}/submit`, { data }),

    // Validate form
    validateForm: (formId: string, data: Record<string, unknown>) =>
        api.post(`/forms/${formId}/validate`, { data }),
}

export default api
