import { Outlet, Link, useLocation } from 'react-router-dom'
import { Home, FileCode, Table2, Settings, Menu } from 'lucide-react'
import { useState } from 'react'

const navItems = [
    { path: '/', icon: Home, label: 'Dashboard' },
    { path: '/processes', icon: FileCode, label: 'Processes' },
    { path: '/modeler/bpmn', icon: FileCode, label: 'BPMN Modeler' },
    { path: '/modeler/dmn', icon: Table2, label: 'DMN Modeler' },
]

export default function Layout() {
    const location = useLocation()
    const [sidebarOpen, setSidebarOpen] = useState(true)

    return (
        <div className="flex h-screen bg-slate-900">
            {/* Sidebar */}
            <aside
                className={`${sidebarOpen ? 'w-64' : 'w-16'} bg-slate-800 border-r border-slate-700 transition-all duration-300 flex flex-col`}
            >
                {/* Logo */}
                <div className="h-16 flex items-center justify-between px-4 border-b border-slate-700">
                    {sidebarOpen && (
                        <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-indigo-500 bg-clip-text text-transparent">
                            Workflow
                        </span>
                    )}
                    <button
                        onClick={() => setSidebarOpen(!sidebarOpen)}
                        className="p-2 rounded-lg hover:bg-slate-700 text-slate-400 hover:text-white transition-colors"
                    >
                        <Menu size={20} />
                    </button>
                </div>

                {/* Navigation */}
                <nav className="flex-1 py-4">
                    {navItems.map((item) => {
                        const isActive = location.pathname === item.path
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`flex items-center gap-3 px-4 py-3 mx-2 rounded-lg transition-all duration-200 ${isActive
                                        ? 'bg-blue-600 text-white shadow-lg shadow-blue-600/30'
                                        : 'text-slate-400 hover:bg-slate-700 hover:text-white'
                                    }`}
                            >
                                <item.icon size={20} />
                                {sidebarOpen && <span className="font-medium">{item.label}</span>}
                            </Link>
                        )
                    })}
                </nav>

                {/* Settings */}
                <div className="p-4 border-t border-slate-700">
                    <Link
                        to="/settings"
                        className="flex items-center gap-3 text-slate-400 hover:text-white transition-colors"
                    >
                        <Settings size={20} />
                        {sidebarOpen && <span>Settings</span>}
                    </Link>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-auto">
                <Outlet />
            </main>
        </div>
    )
}
