import { useAuth } from '../../hooks/useAuth';
import { useEffect, useState } from 'react';
import {
  BookOpen,
  Clock,
  Award,
  GraduationCap,
  Eye,
  EyeOff,
  Users,
  BarChart3,
} from 'lucide-react';
import toast from 'react-hot-toast';
import { getStudentEnrollments } from '../../services/enrollmentService';
import { getCoursesByInstructor } from '../../services/courseService';
import { getAdminStats } from '../../services/adminService';
import type { Enrollment, Course, AdminStats } from '../../types/api';

export default function DashboardPage() {
  const { user } = useAuth();
  const [studentEnrollments, setStudentEnrollments] = useState<Enrollment[]>([]);
  const [instructorCourses, setInstructorCourses] = useState<Course[]>([]);
  const [adminStats, setAdminStats] = useState<AdminStats | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!user) return;
      setIsLoading(true);
      setError(null);

      try {
        if (user.role === 'STUDENT') {
          const data = await getStudentEnrollments(user.id);
          setStudentEnrollments(data);
        } else if (user.role === 'INSTRUCTOR') {
          const response = await getCoursesByInstructor(user.id, { page: 0, size: 50 });
          setInstructorCourses(response.courses);
        } else if (user.role === 'ADMIN') {
          const stats = await getAdminStats();
          setAdminStats(stats);
        }
      } catch (err: any) {
        const message = err.response?.data?.message || 'Failed to load dashboard data';
        setError(message);
        toast.error(message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [user]);

  const renderStudentDashboard = () => {
    const active = studentEnrollments.filter((enrollment) => enrollment.status === 'ACTIVE');
    const completed = studentEnrollments.filter((enrollment) => enrollment.status === 'COMPLETED');
    const mostRecent = studentEnrollments
      .slice()
      .sort((a, b) => new Date(b.enrolledAt).getTime() - new Date(a.enrolledAt).getTime())[0];

    return (
      <>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <DashboardCard
            title="Active Courses"
            value={active.length}
            subtitle="Currently enrolled courses"
            icon={<BookOpen className="h-6 w-6 text-white" />}
            gradient="from-purple-600 to-pink-600"
          />
          <DashboardCard
            title="Completed Courses"
            value={completed.length}
            subtitle="All time"
            icon={<Award className="h-6 w-6 text-white" />}
            gradient="from-green-600 to-emerald-600"
          />
          <DashboardCard
            title="Total Enrollments"
            value={studentEnrollments.length}
            subtitle="Keep learning!"
            icon={<Clock className="h-6 w-6 text-white" />}
            gradient="from-blue-600 to-cyan-600"
          />
        </div>

        <div className="mt-8 bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
          <h2 className="text-2xl font-bold text-gray-200 mb-4">Recent Activity</h2>
          {mostRecent ? (
            <p className="text-gray-300">
              Last enrollment on{' '}
              {new Date(mostRecent.enrolledAt).toLocaleDateString(undefined, {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
              })}
              . Keep up the great work!
            </p>
          ) : (
            <p className="text-gray-400">No enrollments yet.</p>
          )}
        </div>
      </>
    );
  };

  const renderInstructorDashboard = () => {
    const published = instructorCourses.filter((course) => course.published);
    const drafts = instructorCourses.filter((course) => !course.published);
    const latestCourses = instructorCourses
      .slice()
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 3);

    return (
      <>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <DashboardCard
            title="Total Courses"
            value={instructorCourses.length}
            subtitle="Courses you've created"
            icon={<GraduationCap className="h-6 w-6 text-white" />}
            gradient="from-purple-600 to-indigo-600"
          />
          <DashboardCard
            title="Published"
            value={published.length}
            subtitle="Visible to students"
            icon={<Eye className="h-6 w-6 text-white" />}
            gradient="from-green-600 to-emerald-600"
          />
          <DashboardCard
            title="Drafts"
            value={drafts.length}
            subtitle="Need more work"
            icon={<EyeOff className="h-6 w-6 text-white" />}
            gradient="from-yellow-600 to-orange-600"
          />
        </div>

        <div className="mt-8 bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
          <h2 className="text-2xl font-bold text-gray-200 mb-4">Latest Courses</h2>
          {latestCourses.length === 0 ? (
            <p className="text-gray-400">You haven't created any courses yet.</p>
          ) : (
            <div className="space-y-3">
              {latestCourses.map((course) => (
                <div
                  key={course.id}
                  className="flex items-center justify-between border border-gray-700 rounded-xl p-4"
                >
                  <div>
                    <p className="text-gray-100 font-semibold">{course.title}</p>
                    <p className="text-xs text-gray-500">
                      Created on {new Date(course.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                  <span
                    className={`px-2 py-1 text-xs rounded-full ${
                      course.published
                        ? 'bg-green-500/10 text-green-400 border border-green-500/20'
                        : 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/20'
                    }`}
                  >
                    {course.published ? 'Published' : 'Draft'}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </>
    );
  };

  const renderAdminDashboard = () => (
    <>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <DashboardCard
          title="Total Courses"
          value={adminStats?.totalCourses ?? 0}
          subtitle="Across the platform"
          icon={<BookOpen className="h-6 w-6 text-white" />}
          gradient="from-purple-600 to-pink-600"
        />
        <DashboardCard
          title="Active Courses"
          value={adminStats?.activeCourses ?? 0}
          subtitle="Currently visible"
          icon={<Eye className="h-6 w-6 text-white" />}
          gradient="from-green-600 to-emerald-600"
        />
        <DashboardCard
          title="Inactive Courses"
          value={adminStats?.inactiveCourses ?? 0}
          subtitle="Hidden or drafts"
          icon={<EyeOff className="h-6 w-6 text-white" />}
          gradient="from-yellow-600 to-orange-600"
        />
        <DashboardCard
          title="Total Users"
          value={adminStats?.totalUsers ?? 0}
          subtitle="All roles combined"
          icon={<Users className="h-6 w-6 text-white" />}
          gradient="from-blue-600 to-cyan-600"
        />
      </div>

      <div className="mt-8 bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
        <div className="flex items-center gap-3 mb-4">
          <BarChart3 className="h-6 w-6 text-purple-400" />
          <h2 className="text-2xl font-bold text-gray-200">Platform Health</h2>
        </div>
        <p className="text-gray-300">
          Use the admin panels to dive deeper into course performance and user management.
        </p>
      </div>
    </>
  );

  if (!user) {
    return <div className="text-gray-400">Loading dashboard...</div>;
  }

  return (
    <div>
      <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent mb-8">
        Welcome back, {user.username}!
      </h1>

      {error && (
        <div className="mb-6 bg-red-900/20 border border-red-700/40 rounded-xl p-4 text-red-200">
          {error}
        </div>
      )}

      {isLoading ? (
        <div className="text-gray-400">Loading personalized dashboard...</div>
      ) : (
        <>
          {user.role === 'STUDENT' && renderStudentDashboard()}
          {user.role === 'INSTRUCTOR' && renderInstructorDashboard()}
          {user.role === 'ADMIN' && renderAdminDashboard()}
        </>
      )}
    </div>
  );
}

interface DashboardCardProps {
  title: string;
  value: number | string;
  subtitle: string;
  icon: React.ReactNode;
  gradient: string;
}

function DashboardCard({ title, value, subtitle, icon, gradient }: DashboardCardProps) {
  return (
    <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
      <div className="flex items-center gap-4 mb-4">
        <div className={`bg-gradient-to-br ${gradient} p-3 rounded-xl`}>{icon}</div>
        <h3 className="text-lg font-semibold text-gray-200">{title}</h3>
      </div>
      <p className="text-4xl font-bold text-gray-100">{value}</p>
      <p className="text-sm text-gray-400 mt-2">{subtitle}</p>
    </div>
  );
}
