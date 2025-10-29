import { useAuth } from '../../hooks/useAuth';
import { BookOpen, Clock, Award } from 'lucide-react';

export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <div>
      <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent mb-8">
        Welcome back, {user?.username}!
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl hover:scale-[1.02] transition-transform duration-200">
          <div className="flex items-center gap-4 mb-4">
            <div className="bg-gradient-to-br from-purple-600 to-pink-600 p-3 rounded-xl">
              <BookOpen className="h-6 w-6 text-white" />
            </div>
            <h3 className="text-lg font-semibold text-gray-200">Active Courses</h3>
          </div>
          <p className="text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
            0
          </p>
          <p className="text-sm text-gray-400 mt-2">Currently enrolled</p>
        </div>

        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl hover:scale-[1.02] transition-transform duration-200">
          <div className="flex items-center gap-4 mb-4">
            <div className="bg-gradient-to-br from-blue-600 to-cyan-600 p-3 rounded-xl">
              <Clock className="h-6 w-6 text-white" />
            </div>
            <h3 className="text-lg font-semibold text-gray-200">Last Attempted</h3>
          </div>
          <p className="text-xl font-bold text-gray-300">
            No courses yet
          </p>
          <p className="text-sm text-gray-400 mt-2">Start learning today!</p>
        </div>

        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl hover:scale-[1.02] transition-transform duration-200">
          <div className="flex items-center gap-4 mb-4">
            <div className="bg-gradient-to-br from-yellow-600 to-orange-600 p-3 rounded-xl">
              <Award className="h-6 w-6 text-white" />
            </div>
            <h3 className="text-lg font-semibold text-gray-200">Certificates</h3>
          </div>
          <p className="text-4xl font-bold bg-gradient-to-r from-yellow-400 to-orange-400 bg-clip-text text-transparent">
            0
          </p>
          <p className="text-sm text-gray-400 mt-2">Earned certificates</p>
        </div>
      </div>

      <div className="mt-8 bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
        <h2 className="text-2xl font-bold text-gray-200 mb-4">Recent Activity</h2>
        <p className="text-gray-400">No recent activity to display</p>
      </div>
    </div>
  );
}