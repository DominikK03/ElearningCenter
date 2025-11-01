import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, BookOpen, Trash2 } from 'lucide-react';
import Badge from '../ui/Badge';
import Button from '../ui/Button';
import type { Enrollment, PublicCourse } from '../../types/api';
import { getCourseById } from '../../services/courseService';

interface EnrollmentCardProps {
  enrollment: Enrollment;
  onUnenroll: (enrollmentId: number) => void;
}

export default function EnrollmentCard({ enrollment, onUnenroll }: EnrollmentCardProps) {
  const [course, setCourse] = useState<PublicCourse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCourse();
  }, [enrollment.courseId]);

  const fetchCourse = async () => {
    try {
      setLoading(true);
      const response = await getCourseById(enrollment.courseId);
      setCourse(response);
    } catch (err) {
      console.error('Error fetching course:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = () => {
    switch (enrollment.status) {
      case 'ACTIVE':
        return <Badge variant="success">Active</Badge>;
      case 'COMPLETED':
        return <Badge variant="purple">Completed</Badge>;
      case 'DROPPED':
        return <Badge variant="danger">Dropped</Badge>;
      default:
        return null;
    }
  };

  if (loading) {
    return (
      <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 animate-pulse">
        <div className="flex gap-4">
          <div className="w-48 h-32 bg-gray-700 rounded-lg"></div>
          <div className="flex-1 space-y-3">
            <div className="h-6 bg-gray-700 rounded w-3/4"></div>
            <div className="h-4 bg-gray-700 rounded w-1/2"></div>
            <div className="h-2 bg-gray-700 rounded w-full"></div>
          </div>
        </div>
      </div>
    );
  }

  if (!course) {
    return null;
  }

  return (
    <div className="bg-gray-800 border border-gray-700 rounded-xl overflow-hidden hover:border-purple-500 transition-colors">
      <div className="flex flex-col sm:flex-row gap-4 p-6">
        {/* Thumbnail */}
        <div className="w-full sm:w-48 h-32 flex-shrink-0">
          {course.thumbnailUrl ? (
            <img
              src={course.thumbnailUrl}
              alt={course.title}
              className="w-full h-full object-cover rounded-lg"
            />
          ) : (
            <div className="w-full h-full bg-gradient-to-br from-purple-900/40 to-pink-900/40 rounded-lg flex items-center justify-center">
              <BookOpen className="w-12 h-12 text-purple-400 opacity-50" />
            </div>
          )}
        </div>

        {/* Content */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-4 mb-2">
            <div className="flex-1 min-w-0">
              <h3 className="text-xl font-semibold text-gray-100 truncate">
                {course.title}
              </h3>
              <p className="text-sm text-gray-400 mt-1">
                {course.instructorName}
              </p>
            </div>
            {getStatusBadge()}
          </div>

          {/* Progress Bar */}
          <div className="mt-4">
            <div className="flex justify-between items-center mb-2">
              <span className="text-sm text-gray-400">Progress</span>
              <span className="text-sm font-semibold text-gray-100">
                {enrollment.progressPercentage}%
              </span>
            </div>
            <div className="w-full bg-gray-700 rounded-full h-2">
              <div
                className="bg-gradient-to-r from-purple-500 to-pink-500 h-2 rounded-full transition-all duration-300"
                style={{ width: `${enrollment.progressPercentage}%` }}
              />
            </div>
          </div>

          {/* Metadata */}
          <div className="flex items-center gap-4 mt-4 text-sm text-gray-400">
            <div className="flex items-center gap-1">
              <Calendar className="w-4 h-4" />
              <span>Enrolled: {new Date(enrollment.enrolledAt).toLocaleDateString()}</span>
            </div>
            {enrollment.completedAt && (
              <div className="flex items-center gap-1">
                <Calendar className="w-4 h-4" />
                <span>Completed: {new Date(enrollment.completedAt).toLocaleDateString()}</span>
              </div>
            )}
          </div>

          {/* Actions */}
          <div className="flex gap-3 mt-4">
            <Link to={`/dashboard/course/${course.id}`} className="flex-1 sm:flex-initial">
              <Button variant="primary" className="w-full sm:w-auto">
                {enrollment.status === 'COMPLETED' ? 'Review Course' : 'Continue Learning'}
              </Button>
            </Link>
            {enrollment.status === 'ACTIVE' && (
              <Button
                variant="danger"
                onClick={() => onUnenroll(enrollment.id)}
                className="px-4"
              >
                <Trash2 className="w-4 h-4" />
              </Button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
