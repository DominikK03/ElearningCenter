import { useState } from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import {
  LayoutDashboard,
  BookOpen,
  GraduationCap,
  User,
  LogOut,
  Menu,
  X,
  Users,
  BarChart3,
  PlusCircle,
  Settings,
  Wallet
} from 'lucide-react';

interface NavItem {
  label: string;
  path: string;
  icon: React.ReactNode;
}

function DashboardLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const getNavigationItems = (): NavItem[] => {
    if (!user) return [];

    const commonItems: NavItem[] = [
      { label: 'Dashboard', path: '/dashboard', icon: <LayoutDashboard className="h-5 w-5" /> },
      { label: 'Balance', path: '/balance', icon: <Wallet className="h-5 w-5" /> },
    ];

    const roleSpecificItems: Record<string, NavItem[]> = {
      STUDENT: [
        { label: 'Browse Courses', path: '/courses', icon: <BookOpen className="h-5 w-5" /> },
        { label: 'My Courses', path: '/my-courses', icon: <GraduationCap className="h-5 w-5" /> },
        { label: 'Profile', path: '/profile', icon: <User className="h-5 w-5" /> },
      ],
      INSTRUCTOR: [
        { label: 'My Teaching', path: '/my-teaching', icon: <GraduationCap className="h-5 w-5" /> },
        { label: 'Create Course', path: '/create-course', icon: <PlusCircle className="h-5 w-5" /> },
        { label: 'Browse Courses', path: '/courses', icon: <BookOpen className="h-5 w-5" /> },
        { label: 'Profile', path: '/profile', icon: <User className="h-5 w-5" /> },
      ],
      ADMIN: [
        { label: 'Admin Dashboard', path: '/admin', icon: <BarChart3 className="h-5 w-5" /> },
        { label: 'Manage Users', path: '/admin/users', icon: <Users className="h-5 w-5" /> },
        { label: 'Manage Courses', path: '/admin/courses', icon: <Settings className="h-5 w-5" /> },
        { label: 'Profile', path: '/profile', icon: <User className="h-5 w-5" /> },
      ],
    };

    return [...commonItems, ...(roleSpecificItems[user.role] || [])];
  };

  const navItems = getNavigationItems();

  return (
    <div className="min-h-screen bg-gray-950">
      {/* Header */}
      <header className="fixed top-0 left-0 right-0 z-50 bg-gray-900/95 backdrop-blur-sm border-b border-gray-800">
        <div className="flex items-center justify-between px-4 py-3">
          {/* Mobile Menu Button */}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="lg:hidden p-2 text-gray-300 hover:bg-gray-800 rounded-lg transition-colors"
          >
            {sidebarOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>

          {/* Logo */}
          <Link to="/dashboard" className="flex items-center gap-2">
            <div className="bg-gradient-to-r from-purple-600 to-pink-600 p-2 rounded-xl">
              <GraduationCap className="h-6 w-6 text-white" />
            </div>
            <span className="text-xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
              E-Learning Center
            </span>
          </Link>

          {/* User Menu */}
          <div className="flex items-center gap-4">
            {user && (
              <>
                <div className="hidden sm:flex items-center gap-3 px-4 py-2 bg-gray-800 rounded-xl border border-gray-700">
                  <div className="text-right">
                    <p className="text-sm font-semibold text-gray-200">{user.username}</p>
                    <p className="text-xs text-gray-400">{user.role}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-gray-400">Balance (PLN)</p>
                    <p className="text-sm font-bold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent">
                      PLN {user.balance.toFixed(2)}
                    </p>
                  </div>
                </div>

                <button
                  onClick={handleLogout}
                  className="p-2 text-gray-300 hover:bg-red-600/20 hover:text-red-400 rounded-lg transition-all duration-200"
                  title="Logout"
                >
                  <LogOut className="h-5 w-5" />
                </button>
              </>
            )}
          </div>
        </div>
      </header>

      {/* Sidebar */}
      <aside
        className={`fixed top-16 left-0 bottom-0 z-40 w-64 bg-gray-900/95 backdrop-blur-sm border-r border-gray-800 transition-transform duration-300 lg:translate-x-0 ${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <nav className="flex flex-col gap-1 p-4">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              onClick={() => setSidebarOpen(false)}
              className="flex items-center gap-3 px-4 py-3 text-gray-300 hover:bg-gradient-to-r hover:from-purple-600/20 hover:to-pink-600/20 hover:text-white rounded-xl transition-all duration-200 group"
            >
              <span className="group-hover:scale-110 transition-transform duration-200">
                {item.icon}
              </span>
              <span className="font-medium">{item.label}</span>
            </Link>
          ))}
        </nav>
      </aside>

      {/* Mobile Sidebar Overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main Content */}
      <main className="pt-16 lg:pl-64 min-h-screen">
        <div className="p-6">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default DashboardLayout;
