import { useState, useEffect } from 'react';
import { BookOpen, Filter, ArrowUpDown } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../../contexts/AuthContext';
import { getStudentEnrollments, unenrollFromCourse } from '../../services/enrollmentService';
import EnrollmentCard from '../../components/student/EnrollmentCard';
import Button from '../../components/ui/Button';
import Skeleton from '../../components/ui/Skeleton';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import type { Enrollment, EnrollmentStatus } from '../../types/api';

type SortOption = 'progress' | 'enrolledAt' | 'status';

export default function MyEnrollmentsPage() {
  const { user } = useAuth();
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [filteredEnrollments, setFilteredEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<EnrollmentStatus | 'ALL'>('ALL');
  const [sortBy, setSortBy] = useState<SortOption>('enrolledAt');
  const [sortAsc, setSortAsc] = useState(false);
  const [unenrollId, setUnenrollId] = useState<number | null>(null);
  const [isUnenrolling, setIsUnenrolling] = useState(false);

  useEffect(() => {
    if (user) {
      fetchEnrollments();
    }
  }, [user]);

  useEffect(() => {
    applyFiltersAndSort();
  }, [enrollments, statusFilter, sortBy, sortAsc]);

  const fetchEnrollments = async () => {
    if (!user) return;

    try {
      setLoading(true);
      const enrollmentsData = await getStudentEnrollments(user.id);
      setEnrollments(enrollmentsData || []);
    } catch (err: any) {
      console.error('Error fetching enrollments:', err);
      toast.error(err.response?.data?.message || 'Failed to load enrollments');
    } finally {
      setLoading(false);
    }
  };

  const applyFiltersAndSort = () => {
    let filtered = [...enrollments];

    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(e => e.status === statusFilter);
    }

    filtered.sort((a, b) => {
      let comparison = 0;

      switch (sortBy) {
        case 'progress':
          comparison = a.progressPercentage - b.progressPercentage;
          break;
        case 'enrolledAt':
          comparison = new Date(a.enrolledAt).getTime() - new Date(b.enrolledAt).getTime();
          break;
        case 'status':
          comparison = a.status.localeCompare(b.status);
          break;
      }

      return sortAsc ? comparison : -comparison;
    });

    setFilteredEnrollments(filtered);
  };

  const handleUnenroll = (enrollmentId: number) => {
    setUnenrollId(enrollmentId);
  };

  const confirmUnenroll = async () => {
    if (!unenrollId) return;

    try {
      setIsUnenrolling(true);
      await unenrollFromCourse(unenrollId);
      toast.success('Successfully unenrolled from course');
      setUnenrollId(null);
      await fetchEnrollments();
    } catch (err: any) {
      console.error('Error unenrolling:', err);
      toast.error(err.response?.data?.message || 'Failed to unenroll from course');
    } finally {
      setIsUnenrolling(false);
    }
  };

  const toggleSort = (option: SortOption) => {
    if (sortBy === option) {
      setSortAsc(!sortAsc);
    } else {
      setSortBy(option);
      setSortAsc(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-950">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <Skeleton className="h-10 w-64 mb-8" />
          <div className="space-y-4">
            <Skeleton className="h-48 w-full" />
            <Skeleton className="h-48 w-full" />
            <Skeleton className="h-48 w-full" />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-950">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Header */}
        <div className="flex items-center gap-3 mb-8">
          <BookOpen className="w-8 h-8 text-purple-400" />
          <h1 className="text-3xl font-bold text-gray-100">My Enrollments</h1>
        </div>

        {/* Filters and Sort */}
        <div className="bg-gray-800 border border-gray-700 rounded-xl p-4 mb-6">
          <div className="flex flex-col sm:flex-row gap-4">
            {/* Status Filter */}
            <div className="flex-1">
              <label className="flex items-center gap-2 text-sm text-gray-400 mb-2">
                <Filter className="w-4 h-4" />
                Status
              </label>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value as EnrollmentStatus | 'ALL')}
                className="w-full bg-gray-900 border border-gray-700 text-gray-100 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="ALL">All</option>
                <option value="ACTIVE">Active</option>
                <option value="COMPLETED">Completed</option>
                <option value="DROPPED">Dropped</option>
              </select>
            </div>

            {/* Sort Options */}
            <div className="flex-1">
              <label className="flex items-center gap-2 text-sm text-gray-400 mb-2">
                <ArrowUpDown className="w-4 h-4" />
                Sort By
              </label>
              <div className="flex gap-2">
                <Button
                  variant={sortBy === 'enrolledAt' ? 'primary' : 'secondary'}
                  onClick={() => toggleSort('enrolledAt')}
                  className="flex-1 text-sm"
                >
                  Date {sortBy === 'enrolledAt' && (sortAsc ? '↑' : '↓')}
                </Button>
                <Button
                  variant={sortBy === 'progress' ? 'primary' : 'secondary'}
                  onClick={() => toggleSort('progress')}
                  className="flex-1 text-sm"
                >
                  Progress {sortBy === 'progress' && (sortAsc ? '↑' : '↓')}
                </Button>
                <Button
                  variant={sortBy === 'status' ? 'primary' : 'secondary'}
                  onClick={() => toggleSort('status')}
                  className="flex-1 text-sm"
                >
                  Status {sortBy === 'status' && (sortAsc ? '↑' : '↓')}
                </Button>
              </div>
            </div>
          </div>
        </div>

        {/* Enrollments List */}
        {filteredEnrollments.length === 0 ? (
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center">
            <BookOpen className="w-16 h-16 text-gray-600 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-300 mb-2">
              {statusFilter === 'ALL' ? 'No enrollments yet' : `No ${statusFilter.toLowerCase()} enrollments`}
            </h3>
            <p className="text-gray-500">
              {statusFilter === 'ALL'
                ? 'Start learning by enrolling in a course!'
                : 'Try changing the filter to see other enrollments.'}
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredEnrollments.map((enrollment) => (
              <EnrollmentCard
                key={enrollment.id}
                enrollment={enrollment}
                onUnenroll={handleUnenroll}
              />
            ))}
          </div>
        )}
      </div>

      {/* Unenroll Confirmation Dialog */}
      <ConfirmDialog
        isOpen={unenrollId !== null}
        onClose={() => setUnenrollId(null)}
        onConfirm={confirmUnenroll}
        title="Unenroll from Course"
        message="Are you sure you want to unenroll from this course? Your progress will be lost."
        confirmText="Unenroll"
        cancelText="Cancel"
        variant="danger"
        isLoading={isUnenrolling}
      />
    </div>
  );
}
