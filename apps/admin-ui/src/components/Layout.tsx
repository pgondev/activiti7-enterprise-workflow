import { Outlet, Link, useLocation } from 'react-router-dom'
import { LayoutDashboard, Activity, Package, Users, Settings, Shield } from 'lucide-react'

const navItems = [
    { path: '/', icon: LayoutDashboard, label: 'Dashboard' },
    { path: '/instances', icon: Activity, label: 'Process Instances' },
    { path: '/deployments', icon: Package, label: 'Deployments' },
    { path: '/users', icon: Users, label: 'Users' },
]

export default function Layout() {
    const location = useLocation()

    return (
        <div className="flex h-screen bg-slate-900">
            {/* Sidebar */}
            <aside className="w-64 bg-slate-800 border-r border-slate-700 flex flex-col">
                {/* Logo */}
                <div className="h-16 flex items-center gap-3 px-6 border-b border-slate-700">
                    <div className="p-2 bg-indigo-600 rounded-lg">
                        <Shield size={20} className="text-white" />
                    </div>
                    <span className="text-lg font-bold text-white">Admin Console</span>
                </div>

                {/* Navigation */}
                <nav className="flex-1 py-4">
                    {navItems.map((item) => {
                        const isActive = location.pathname === item.path
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`flex items-center gap-3 px-6 py-3 transition-all duration-200 ${isActive
                                        ? 'bg-indigo-600/20 text-indigo-400 border-r-2 border-indigo-500'
                                        : 'text-slate-400 hover:bg-slate-700/50 hover:text-white'
                                    }`}
                            >
                                <item.icon size={20} />
                                <span className="font-medium">{item.label}</span>
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
                        <span>Settings</span>
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
