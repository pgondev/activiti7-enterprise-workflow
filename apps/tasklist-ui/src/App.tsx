import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import TaskInbox from './pages/TaskInbox'
import TaskDetail from './pages/TaskDetail'
import MyTasks from './pages/MyTasks'

function App() {
    return (
        <Routes>
            <Route path="/" element={<Layout />}>
                <Route index element={<TaskInbox />} />
                <Route path="my-tasks" element={<MyTasks />} />
                <Route path="tasks/:taskId" element={<TaskDetail />} />
            </Route>
        </Routes>
    )
}

export default App
