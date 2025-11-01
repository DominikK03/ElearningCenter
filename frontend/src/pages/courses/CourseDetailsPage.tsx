import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
  BookOpen,
  Clock,
  User,
  PlayCircle,
  FileText,
  Calendar,
  ArrowLeft,
  ShoppingCart,
} from 'lucide-react';
import { getCourseById } from '../../services/courseService';
import { enrollInCourse } from '../../services/enrollmentService';
import type { PublicCourseDetails } from '../../types/api';
import { useAuth } from '../../contexts/AuthContext';
import Badge from '../../components/ui/Badge';
import Button from '../../components/ui/Button';
import { Accordion, AccordionItem } from '../../components/ui/Accordion';
import Skeleton from '../../components/ui/Skeleton';
import EnrollmentConfirmDialog from '../../components/student/EnrollmentConfirmDialog';

export default function CourseDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<PublicCourseDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showEnrollDialog, setShowEnrollDialog] = useState(false);
  const [isEnrolling, setIsEnrolling] = useState(false);

  useEffect(() => {
    if (id) {
      fetchCourse(parseInt(id));
    }
  }, [id]);

  const fetchCourse = async (courseId: number) => {
    try {
      setLoading(true);
      setError(null);

      const response = await getCourseById(courseId);
      setCourse(response);
    } catch (err: any) {
      console.error('Error fetching course:', err);
      setError(err.response?.data?.message || 'Failed to load course details');
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = () => {
    if (!user) {
      navigate('/login', { state: { from: `/courses/${id}` } });
      return;
    }

    if (user.role !== 'STUDENT') {
      toast.error('Only students can enroll in courses');
      return;
    }

    setShowEnrollDialog(true);
  };

  const handleConfirmEnrollment = async () => {
    if (!user || !course) return;

    try {
      setIsEnrolling(true);
      await enrollInCourse({ studentId: user.id, courseId: course.id });
      toast.success('Successfully enrolled in course!');
      setShowEnrollDialog(false);
      navigate(`/dashboard/course/${course.id}`);
    } catch (err: any) {
      console.error('Error enrolling:', err);
      const errorMessage = err.response?.data?.message || 'Failed to enroll in course';
      toast.error(errorMessage);
    } finally {
      setIsEnrolling(false);
    }
  };

  const levelColors = {
    BEGINNER: 'success' as const,
    INTERMEDIATE: 'warning' as const,
    ADVANCED: 'danger' as const,
  };

  const levelLabels = {
    BEGINNER: 'Beginner',
    INTERMEDIATE: 'Intermediate',
    ADVANCED: 'Advanced',
  };

  const calculateTotalDuration = () => {
    return 0;
  };

  const formatDuration = (minutes: number) => {
    if (minutes < 60) return `${minutes} minutes`;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins > 0 ? `${hours}h ${mins}m` : `${hours} hours`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-950">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <Skeleton className="h-8 w-24 mb-8" />
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-6">
              <Skeleton className="h-96 w-full rounded-xl" />
              <Skeleton className="h-12 w-3/4" />
              <Skeleton className="h-24 w-full" />
            </div>
            <div>
              <Skeleton className="h-64 w-full rounded-xl" />
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !course) {
    return (
      <div className="min-h-screen bg-gray-950">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="bg-red-900/30 border border-red-700 text-red-400 px-6 py-4 rounded-xl">
            <p className="font-medium">Error loading course</p>
            <p className="text-sm mt-1">{error || 'Course not found'}</p>
            <Link to="/courses" className="text-sm text-purple-400 hover:text-purple-300 mt-4 inline-block">
              ← Back to courses
            </Link>
          </div>
        </div>
      </div>
    );
  }

  const totalDuration = calculateTotalDuration();
  const isOwner = false;

  return (
    <div className="min-h-screen bg-gray-950">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <Link
          to="/courses"
          className="inline-flex items-center gap-2 text-gray-400 hover:text-gray-300 mb-8 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          Back to courses
        </Link>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-8">
            <div className="bg-gray-800 border border-gray-700 rounded-xl overflow-hidden">
              <div className="relative w-full h-96 bg-gradient-to-br from-purple-900/40 to-pink-900/40">
                {course.thumbnailUrl ? (
                  <img
                    src={course.thumbnailUrl}
                    alt={course.title}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <BookOpen className="w-24 h-24 text-purple-400 opacity-50" />
                  </div>
                )}
                {!course.published && (
                  <div className="absolute top-4 right-4">
                    <Badge variant="warning">Unpublished</Badge>
                  </div>
                )}
              </div>

              <div className="p-6">
                <div className="flex flex-wrap gap-2 mb-4">
                  <Badge variant={levelColors[course.level]}>{levelLabels[course.level]}</Badge>
                  <Badge variant="purple">{course.category}</Badge>
                </div>

                <h1 className="text-4xl font-bold text-gray-100 mb-4">{course.title}</h1>

                <p className="text-lg text-gray-300 leading-relaxed">{course.description}</p>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6 pt-6 border-t border-gray-700">
                  <div className="flex items-center gap-2 text-gray-400">
                    <BookOpen className="w-5 h-5 text-purple-400" />
                    <div>
                      <div className="text-sm">Sections</div>
                      <div className="text-xl font-bold text-gray-100">{course.sectionsCount}</div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 text-gray-400">
                    <PlayCircle className="w-5 h-5 text-purple-400" />
                    <div>
                      <div className="text-sm">Lessons</div>
                      <div className="text-xl font-bold text-gray-100">
                        {course.totalLessonsCount}
                      </div>
                    </div>
                  </div>

                  {totalDuration > 0 && (
                    <div className="flex items-center gap-2 text-gray-400">
                      <Clock className="w-5 h-5 text-purple-400" />
                      <div>
                        <div className="text-sm">Duration</div>
                        <div className="text-xl font-bold text-gray-100">
                          {formatDuration(totalDuration)}
                        </div>
                      </div>
                    </div>
                  )}

                  <div className="flex items-center gap-2 text-gray-400">
                    <Calendar className="w-5 h-5 text-purple-400" />
                    <div>
                      <div className="text-sm">Created</div>
                      <div className="text-sm font-medium text-gray-100">
                        {new Date(course.createdAt).toLocaleDateString()}
                      </div>
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-3 mt-6 pt-6 border-t border-gray-700">
                  <div className="w-12 h-12 rounded-full bg-gradient-to-br from-purple-600 to-pink-600 flex items-center justify-center">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <div className="text-sm text-gray-400">Instructor</div>
                    <div className="text-lg font-semibold text-gray-100">
                      {course.instructorName}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
              <h2 className="text-2xl font-bold text-gray-100 mb-6 flex items-center gap-2">
                <FileText className="w-6 h-6 text-purple-400" />
                Course Content
              </h2>

              {course.sections.length === 0 ? (
                <div className="text-center py-8 text-gray-500">
                  No sections added yet
                </div>
              ) : (
                <Accordion>
                  {course.sections.map((section, index) => (
                    <AccordionItem
                      key={section.id}
                      title={`Section ${index + 1}: ${section.title}`}
                      defaultOpen={index === 0}
                    >
                      <p className="text-gray-500 text-sm">
                        Lesson details are available after enrollment
                      </p>
                    </AccordionItem>
                  ))}
                </Accordion>
              )}
            </div>
          </div>

          <div className="lg:col-span-1">
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 sticky top-8">
              <div className="text-center mb-6">
                <div className="text-4xl font-bold text-purple-400 mb-2">
                  {course.price} {course.currency}
                </div>
                <div className="text-sm text-gray-500">One-time payment</div>
              </div>

              <div className="space-y-3">
                {isOwner ? (
                  <Link to={`/dashboard/course/${course.id}/manage`}>
                    <Button variant="primary" className="w-full">
                      Manage Course
                    </Button>
                  </Link>
                ) : user?.role === 'STUDENT' ? (
                  <Button
                    variant="primary"
                    className="w-full"
                    onClick={handleEnroll}
                  >
                    <ShoppingCart className="w-5 h-5" />
                    Enroll Now
                  </Button>
                ) : !user ? (
                  <Link to="/login" state={{ from: `/courses/${id}` }}>
                    <Button variant="primary" className="w-full">
                      Login to Enroll
                    </Button>
                  </Link>
                ) : (
                  <div className="text-center text-sm text-gray-500 py-4">
                    Only students can enroll in courses
                  </div>
                )}

                {isOwner && (
                  <Link to={`/courses/${course.id}`}>
                    <Button variant="ghost" className="w-full">
                      View as Student
                    </Button>
                  </Link>
                )}
              </div>

              <div className="mt-6 pt-6 border-t border-gray-700 space-y-3">
                <div className="text-sm font-semibold text-gray-300 mb-3">
                  This course includes:
                </div>
                <div className="flex items-center gap-3 text-sm text-gray-400">
                  <PlayCircle className="w-5 h-5 text-purple-400" />
                  <span>{course.totalLessonsCount} video lessons</span>
                </div>
                {totalDuration > 0 && (
                  <div className="flex items-center gap-3 text-sm text-gray-400">
                    <Clock className="w-5 h-5 text-purple-400" />
                    <span>{formatDuration(totalDuration)} of content</span>
                  </div>
                )}
                <div className="flex items-center gap-3 text-sm text-gray-400">
                  <FileText className="w-5 h-5 text-purple-400" />
                  <span>Downloadable resources</span>
                </div>
                <div className="flex items-center gap-3 text-sm text-gray-400">
                  <BookOpen className="w-5 h-5 text-purple-400" />
                  <span>Lifetime access</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Enrollment Confirmation Dialog */}
      {user && course && (
        <EnrollmentConfirmDialog
          course={course}
          user={user}
          isOpen={showEnrollDialog}
          onClose={() => setShowEnrollDialog(false)}
          onConfirm={handleConfirmEnrollment}
          isEnrolling={isEnrolling}
        />
      )}
    </div>
  );
}
