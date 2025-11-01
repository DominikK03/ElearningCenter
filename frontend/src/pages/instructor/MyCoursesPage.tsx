import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import { getCoursesByInstructor, deleteCourse } from '../../services/courseService';
import Button from '../../components/ui/Button';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import Pagination from '../../components/ui/Pagination';
import InstructorCourseCard from '../../components/instructor/InstructorCourseCard';
import type { Course } from '../../types/api';

export default function MyCoursesPage() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [courses, setCourses] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Delete dialog state
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [courseToDelete, setCourseToDelete] = useState<Course | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const fetchCourses = async () => {
      if (!user) {
        setError('You must be logged in to view your courses');
        setIsLoading(false);
        return;
      }

      if (user.role !== 'INSTRUCTOR' && user.role !== 'ADMIN') {
        setError('Only instructors can access this page');
        setIsLoading(false);
        navigate('/dashboard');
        return;
      }

      try {
        setIsLoading(true);
        setError(null);

        const pageData = await getCoursesByInstructor(user.id, {
          page: currentPage,
          size: 9, // 3x3 grid
        });

        setCourses(pageData.courses);
        setTotalPages(pageData.totalPages);
        setTotalElements(pageData.totalElements);
      } catch (err: any) {
        console.error('Error fetching courses:', err);
        setError(err.response?.data?.message || 'Failed to load courses');
      } finally {
        setIsLoading(false);
      }
    };

    if (user) {
      fetchCourses();
    }
  }, [user, currentPage, navigate]);

  const handleDeleteClick = (course: Course) => {
    setCourseToDelete(course);
    setShowDeleteDialog(true);
  };

  const handleDeleteConfirm = async () => {
    if (!courseToDelete) return;

    try {
      setIsDeleting(true);
      await deleteCourse(courseToDelete.id);
      toast.success('Course deleted successfully!');
      setShowDeleteDialog(false);
      setCourseToDelete(null);

      // Refresh courses list
      const pageData = await getCoursesByInstructor(user!.id, {
        page: currentPage,
        size: 9,
      });
      setCourses(pageData.courses);
      setTotalPages(pageData.totalPages);
      setTotalElements(pageData.totalElements);
    } catch (err: any) {
      console.error('Error deleting course:', err);
      toast.error(err.response?.data?.message || 'Failed to delete course');
    } finally {
      setIsDeleting(false);
    }
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
          <p className="text-gray-400 mt-4">Loading your courses...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-400 text-lg">{error}</p>
          <Button variant="secondary" onClick={() => navigate('/dashboard')} className="mt-4">
            Back to Dashboard
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-4xl font-bold text-gray-100 mb-2">My Courses</h1>
            <p className="text-gray-400">
              Manage your courses and track their performance
            </p>
          </div>
          <Button variant="primary" onClick={() => navigate('/create-course')}>
            + Create New Course
          </Button>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
            <p className="text-gray-400 text-sm mb-1">Total Courses</p>
            <p className="text-3xl font-bold text-gray-100">{totalElements}</p>
          </div>
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
            <p className="text-gray-400 text-sm mb-1">Published</p>
            <p className="text-3xl font-bold text-green-400">
              {courses.filter((c) => c.published).length}
            </p>
          </div>
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
            <p className="text-gray-400 text-sm mb-1">Drafts</p>
            <p className="text-3xl font-bold text-yellow-400">
              {courses.filter((c) => !c.published).length}
            </p>
          </div>
        </div>

        {/* Courses Grid */}
        {courses.length === 0 ? (
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center">
            <p className="text-gray-400 text-lg mb-4">You haven't created any courses yet</p>
            <p className="text-gray-500 mb-6">
              Start sharing your knowledge by creating your first course
            </p>
            <Button variant="primary" onClick={() => navigate('/create-course')}>
              Create Your First Course
            </Button>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
              {courses.map((course) => (
                <InstructorCourseCard
                  key={course.id}
                  course={course}
                  onDelete={handleDeleteClick}
                />
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            )}
          </>
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showDeleteDialog}
        onClose={() => {
          setShowDeleteDialog(false);
          setCourseToDelete(null);
        }}
        onConfirm={handleDeleteConfirm}
        title="Delete Course"
        message={`Are you sure you want to delete "${courseToDelete?.title}"? This will permanently delete all sections, lessons, quizzes, and enrollments. This action cannot be undone.`}
        confirmText="Delete Course"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  );
}
