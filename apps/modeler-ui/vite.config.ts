import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
    plugins: [react()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    server: {
        port: 3000,
        proxy: {
            '/api/v1/forms': {
                target: 'http://localhost:8084',
                changeOrigin: true,
            },
            '/api/v1/decisions': {
                target: 'http://localhost:8085',
                changeOrigin: true,
            },
            '/api/v1/tasks': {
                target: 'http://localhost:8083',
                changeOrigin: true,
            },
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
        },
    },
    build: {
        outDir: 'dist',
        sourcemap: true,
    },
})
