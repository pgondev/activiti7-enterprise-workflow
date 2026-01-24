import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import ProcessInstances from './pages/ProcessInstances'
import Deployments from './pages/Deployments'
import Users from './pages/Users'

function App() {
    return (
        <Routes>
            <Route path="/" element={<Layout />}>
                <Route index element={<Dashboard />} />
                <Route path="instances" element={<ProcessInstances />} />
                <Route path="deployments" element={<Deployments />} />
                <Route path="users" element={<Users />} />
            </Route>
        </Routes>
    )
}

export default App
