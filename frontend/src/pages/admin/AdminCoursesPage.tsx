import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Trash2, EyeOff } from 'lucide-react';
import { getAllCourses, deleteCourse, unpublishCourse } from '../../services/courseService';
import type { Course } from '../../types/api';
import Button from '../../components/ui/Button';
import Modal from '../../components/ui/Modal';

type ModerationAction = 'UNPUBLISH' | 'DELETE' | null;

export default function AdminCoursesPage() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
  const [moderationAction, setModerationAction] = useState<ModerationAction>(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [isModerationModalOpen, setIsModerationModalOpen] = useState(false);
  const [reason, setReason] = useState('');

  const fetchCourses = async () => {
    try {
      setIsLoading(true);
      const response = await getAllCourses({ page: 0, size: 50 });
      setCourses(response.courses);
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to load courses';
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  const openModerationModal = (course: Course, action: ModerationAction) => {
    setSelectedCourse(course);
    setModerationAction(action);
    setReason('');
    setIsModerationModalOpen(true);
  };

  const closeModerationModal = () => {
    setIsModerationModalOpen(false);
    setSelectedCourse(null);
    setModerationAction(null);
    setReason('');
  };

  const handleModerationConfirm = async () => {
    if (!selectedCourse || !moderationAction) return;
    if (!reason.trim()) {
      toast.error('Reason is required for administrator actions');
      return;
    }

    try {
      setIsProcessing(true);
      if (moderationAction === 'UNPUBLISH') {
        await unpublishCourse(selectedCourse.id, reason.trim());
        toast.success('Course unpublished');
      } else {
        await deleteCourse(selectedCourse.id, reason.trim());
        toast.success('Course deleted');
      }
      closeModerationModal();
      fetchCourses();
    } catch (err: any) {
      const message = err.response?.data?.message || 'Action failed';
      toast.error(message);
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <p className="text-sm uppercase tracking-wider text-purple-400 mb-2">Administration</p>
        <h1 className="text-4xl font-bold text-gray-100">Manage Courses</h1>
        <p className="text-gray-400 mt-2">
          Review courses across the platform and adjust visibility or remove inappropriate content.
        </p>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-2xl p-4">
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by title or instructor ID..."
          className="w-full bg-gray-950 border border-gray-800 rounded-xl px-4 py-2 text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
        />
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-2xl overflow-hidden">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 p-4 border-b border-gray-800 text-sm text-gray-400 uppercase tracking-wider">
          <div>Course</div>
          <div>Instructor</div>
          <div>Status</div>
          <div className="text-right">Actions</div>
        </div>

        {isLoading ? (
          <div className="p-6 text-gray-400">Loading courses...</div>
        ) : courses.length === 0 ? (
          <div className="p-6 text-gray-400">No courses found.</div>
        ) : (
          courses
            .filter((course) => {
              if (!search.trim()) return true;
              const term = search.toLowerCase();
              return (
                course.title.toLowerCase().includes(term) ||
                course.instructorId.toString().includes(term)
              );
            })
            .map((course) => (
            <div
              key={course.id}
              className="grid grid-cols-1 md:grid-cols-4 gap-4 p-4 border-b border-gray-800 text-sm"
            >
              <div>
                <p className="font-semibold text-gray-100">{course.title}</p>
                <p className="text-gray-500 text-xs">{course.category}</p>
              </div>
              <div className="text-gray-300">Instructor #{course.instructorId}</div>
              <div>
                <span
                  className={`px-2 py-1 text-xs rounded-full ${
                    course.published
                      ? 'bg-green-500/10 text-green-400 border border-green-500/20'
                      : 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/20'
                  }`}
                >
                  {course.published ? 'Active' : 'Hidden'}
                </span>
              </div>
              <div className="flex justify-end gap-2">
                {course.published && (
                  <Button
                    variant="secondary"
                    className="!px-3"
                    disabled={isProcessing}
                    onClick={() => openModerationModal(course, 'UNPUBLISH')}
                  >
                    <EyeOff className="h-4 w-4 mr-1" />
                    Unpublish
                  </Button>
                )}
                <Button
                  variant="danger"
                  className="!px-3"
                  disabled={isProcessing}
                  onClick={() => openModerationModal(course, 'DELETE')}
                >
                  <Trash2 className="h-4 w-4 mr-1" />
                  Delete
                </Button>
              </div>
            </div>
          ))
        )}
      </div>

      <Modal
        isOpen={isModerationModalOpen}
        onClose={() => {
          if (!isProcessing) closeModerationModal();
        }}
        title={
          moderationAction === 'UNPUBLISH'
            ? `Unpublish "${selectedCourse?.title ?? ''}"`
            : `Delete "${selectedCourse?.title ?? ''}"`
        }
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-400">
            Provide a reason that will be emailed to the instructor explaining this decision.
          </p>
          <textarea
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            rows={4}
            className="w-full bg-gray-900 border border-gray-700 rounded-lg px-4 py-3 text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Reason for this action"
            disabled={isProcessing}
          />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={closeModerationModal} disabled={isProcessing}>
              Cancel
            </Button>
            <Button
              variant={moderationAction === 'DELETE' ? 'danger' : 'primary'}
              onClick={handleModerationConfirm}
              disabled={isProcessing}
            >
              {isProcessing
                ? 'Processing...'
                : moderationAction === 'DELETE'
                  ? 'Delete Course'
                  : 'Unpublish Course'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
