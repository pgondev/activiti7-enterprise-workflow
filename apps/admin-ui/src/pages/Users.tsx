import { useState } from 'react'
import { Search, UserPlus, Shield, User, Mail, MoreVertical } from 'lucide-react'

interface UserData {
    id: string
    username: string
    email: string
    fullName: string
    roles: string[]
    status: 'ACTIVE' | 'INACTIVE'
    lastLogin: string
}

const mockUsers: UserData[] = [
    { id: '1', username: 'john.doe', email: 'john.doe@example.com', fullName: 'John Doe', roles: ['user', 'approver'], status: 'ACTIVE', lastLogin: '2024-01-15T10:00:00' },
    { id: '2', username: 'jane.smith', email: 'jane.smith@example.com', fullName: 'Jane Smith', roles: ['user', 'admin'], status: 'ACTIVE', lastLogin: '2024-01-15T09:30:00' },
    { id: '3', username: 'bob.wilson', email: 'bob.wilson@example.com', fullName: 'Bob Wilson', roles: ['user'], status: 'ACTIVE', lastLogin: '2024-01-14T16:00:00' },
    { id: '4', username: 'alice.brown', email: 'alice.brown@example.com', fullName: 'Alice Brown', roles: ['user', 'manager'], status: 'INACTIVE', lastLogin: '2024-01-10T11:00:00' },
]

const roleColors: Record<string, string> = {
    admin: 'bg-red-500/20 text-red-400',
    manager: 'bg-purple-500/20 text-purple-400',
    approver: 'bg-blue-500/20 text-blue-400',
    user: 'bg-slate-500/20 text-slate-400',
}

export default function Users() {
    const [search, setSearch] = useState('')

    const filteredUsers = mockUsers.filter((user) =>
        user.username.toLowerCase().includes(search.toLowerCase()) ||
        user.fullName.toLowerCase().includes(search.toLowerCase()) ||
        user.email.toLowerCase().includes(search.toLowerCase())
    )

    return (
        <div className="p-8 animate-fade-in">
            {/* Header */}
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-white mb-1">User Management</h1>
                    <p className="text-slate-400">{mockUsers.length} users registered</p>
                </div>
                <button className="flex items-center gap-2 px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors">
                    <UserPlus size={18} />
                    Add User
                </button>
            </div>

            {/* Search */}
            <div className="mb-6">
                <div className="relative max-w-md">
                    <Search size={20} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                    <input
                        type="text"
                        placeholder="Search users..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        className="w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:border-indigo-500"
                    />
                </div>
            </div>

            {/* Users Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {filteredUsers.map((user) => (
                    <div
                        key={user.id}
                        className="bg-slate-800 rounded-xl border border-slate-700 p-5 hover:border-slate-600 transition-colors"
                    >
                        <div className="flex items-start justify-between mb-4">
                            <div className="flex items-center gap-3">
                                <div className="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-full flex items-center justify-center">
                                    <User size={20} className="text-white" />
                                </div>
                                <div>
                                    <h3 className="font-semibold text-white">{user.fullName}</h3>
                                    <p className="text-sm text-slate-400">@{user.username}</p>
                                </div>
                            </div>
                            <button className="p-1 hover:bg-slate-700 rounded text-slate-400 hover:text-white" title="More">
                                <MoreVertical size={18} />
                            </button>
                        </div>

                        <div className="flex items-center gap-2 text-sm text-slate-400 mb-3">
                            <Mail size={14} />
                            {user.email}
                        </div>

                        <div className="flex items-center gap-2 mb-4">
                            <Shield size={14} className="text-slate-400" />
                            <div className="flex flex-wrap gap-1">
                                {user.roles.map((role) => (
                                    <span
                                        key={role}
                                        className={`px-2 py-0.5 text-xs rounded-full ${roleColors[role] || roleColors.user}`}
                                    >
                                        {role}
                                    </span>
                                ))}
                            </div>
                        </div>

                        <div className="flex items-center justify-between pt-3 border-t border-slate-700">
                            <span className={`px-2 py-1 text-xs rounded-full ${user.status === 'ACTIVE' ? 'bg-green-500/20 text-green-400' : 'bg-slate-500/20 text-slate-400'
                                }`}>
                                {user.status}
                            </span>
                            <span className="text-xs text-slate-500">
                                Last login: {new Date(user.lastLogin).toLocaleDateString()}
                            </span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}
