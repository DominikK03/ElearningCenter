import { useEffect, useState } from 'react';
import { Users, BookOpen, Eye, EyeOff } from 'lucide-react';
import toast from 'react-hot-toast';
import { getAdminStats } from '../../services/adminService';
import type { AdminStats } from '../../types/api';

export default function AdminDashboardPage() {
  const [stats, setStats] = useState<AdminStats | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setIsLoading(true);
        const data = await getAdminStats();
        setStats(data);
      } catch (err: any) {
        const message = err.response?.data?.message || 'Failed to load admin stats';
        toast.error(message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchStats();
  }, []);

  const cards = [
    {
      label: 'Total Courses',
      value: stats?.totalCourses ?? 0,
      icon: <BookOpen className="h-6 w-6 text-purple-400" />,
      gradient: 'from-purple-500/20 to-purple-900/10',
    },
    {
      label: 'Active Courses',
      value: stats?.activeCourses ?? 0,
      icon: <Eye className="h-6 w-6 text-green-400" />,
      gradient: 'from-green-500/20 to-green-900/10',
    },
    {
      label: 'Inactive Courses',
      value: stats?.inactiveCourses ?? 0,
      icon: <EyeOff className="h-6 w-6 text-yellow-400" />,
      gradient: 'from-yellow-500/20 to-yellow-900/10',
    },
    {
      label: 'Registered Users',
      value: stats?.totalUsers ?? 0,
      icon: <Users className="h-6 w-6 text-blue-400" />,
      gradient: 'from-blue-500/20 to-blue-900/10',
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <p className="text-sm uppercase tracking-wider text-purple-400 mb-2">Admin Overview</p>
        <h1 className="text-4xl font-bold text-gray-100">Control Center</h1>
        <p className="text-gray-400 mt-2">
          Monitor platform health, manage courses, and support instructors with quick insights.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
        {cards.map((card) => (
          <div
            key={card.label}
            className={`rounded-2xl border border-gray-800 bg-gradient-to-br ${card.gradient} p-6 flex flex-col gap-3`}
          >
            <div className="flex items-center justify-between">
              <p className="text-sm text-gray-400">{card.label}</p>
              <div className="p-2 rounded-xl bg-black/20 border border-white/5">{card.icon}</div>
            </div>
            <p className="text-3xl font-bold text-gray-100">
              {isLoading ? '...' : card.value}
            </p>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6">
          <h2 className="text-xl font-semibold text-gray-100 mb-3">Courses Summary</h2>
          <p className="text-gray-400">
            Active vs inactive distribution helps you track which instructors might need support.
            Consider reviewing drafts that never got published.
          </p>
        </div>
        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6">
          <h2 className="text-xl font-semibold text-gray-100 mb-3">Users Health</h2>
          <p className="text-gray-400">
            Use the Users Management panel to adjust balances, verify accounts, or troubleshoot
            onboarding issues.
          </p>
        </div>
      </div>
    </div>
  );
}
