import { Outlet, Link, useLocation } from 'react-router-dom'
import { Inbox, CheckSquare, User, Bell, LogOut } from 'lucide-react'

const navItems = [
    { path: '/', icon: Inbox, label: 'Task Inbox' },
    { path: '/my-tasks', icon: CheckSquare, label: 'My Tasks' },
]

export default function Layout() {
    const location = useLocation()

    return (
        <div className="min-h-screen bg-slate-900">
            {/* Top Navigation */}
            <header className="h-16 bg-slate-800 border-b border-slate-700 flex items-center justify-between px-6">
                <div className="flex items-center gap-8">
                    <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-indigo-500 bg-clip-text text-transparent">
                        Task Inbox
                    </span>

                    <nav className="flex gap-1">
                        {navItems.map((item) => {
                            const isActive = location.pathname === item.path
                            return (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-all duration-200 ${isActive
                                            ? 'bg-blue-600 text-white'
                                            : 'text-slate-400 hover:bg-slate-700 hover:text-white'
                                        }`}
                                >
                                    <item.icon size={18} />
                                    <span className="text-sm font-medium">{item.label}</span>
                                </Link>
                            )
                        })}
                    </nav>
                </div>

                <div className="flex items-center gap-4">
                    <button className="relative p-2 text-slate-400 hover:text-white transition-colors" title="Notifications">
                        <Bell size={20} />
                        <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
                    </button>

                    <div className="flex items-center gap-3 pl-4 border-l border-slate-700">
                        <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center">
                            <User size={16} className="text-white" />
                        </div>
                        <div className="hidden md:block">
                            <p className="text-sm text-white font-medium">John Doe</p>
                            <p className="text-xs text-slate-400">Workflow User</p>
                        </div>
                        <button className="p-2 text-slate-400 hover:text-white transition-colors" title="Logout">
                            <LogOut size={18} />
                        </button>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="p-6">
                <Outlet />
            </main>
        </div>
    )
}
