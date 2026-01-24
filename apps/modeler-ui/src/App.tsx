import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import BpmnModeler from './pages/BpmnModeler'
import DmnModeler from './pages/DmnModeler'
import ProcessList from './pages/ProcessList'

function App() {
    return (
        <Routes>
            <Route path="/" element={<Layout />}>
                <Route index element={<Dashboard />} />
                <Route path="processes" element={<ProcessList />} />
                <Route path="modeler/bpmn" element={<BpmnModeler />} />
                <Route path="modeler/bpmn/:id" element={<BpmnModeler />} />
                <Route path="modeler/dmn" element={<DmnModeler />} />
                <Route path="modeler/dmn/:id" element={<DmnModeler />} />
            </Route>
        </Routes>
    )
}

export default App
