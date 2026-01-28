import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import BpmnModeler from './pages/BpmnModeler'
import DmnModeler from './pages/DmnModeler'
import DmnList from './pages/DmnList'
import FormModeler from './pages/FormModeler'
import FormList from './pages/FormList'
import ProcessList from './pages/ProcessList'
import ProcessInstanceList from './pages/ProcessInstanceList'
import ProcessInstanceDetails from './pages/ProcessInstanceDetails'
import TaskList from './pages/TaskList'

function App() {
    return (
        <Routes>
            <Route path="/" element={<Layout />}>
                <Route index element={<Dashboard />} />
                <Route path="processes" element={<ProcessList />} />
                <Route path="modeler/bpmn" element={<BpmnModeler />} />
                <Route path="modeler/bpmn/:id" element={<BpmnModeler />} />
                <Route path="modeler/bpmn/:id" element={<BpmnModeler />} />
                <Route path="modeler/instances" element={<ProcessInstanceList />} />
                <Route path="modeler/instances/:id" element={<ProcessInstanceDetails />} />
                <Route path="modeler/tasks" element={<TaskList />} />
                <Route path="modeler/dmns" element={<DmnList />} />
                <Route path="modeler/dmn" element={<DmnModeler />} />
                <Route path="modeler/dmn/:id" element={<DmnModeler />} />
                <Route path="modeler/forms" element={<FormList />} />
                <Route path="modeler/form" element={<FormModeler />} />
                <Route path="modeler/form/:id" element={<FormModeler />} />
            </Route>
        </Routes>
    )
}

export default App
