import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import {
  getFullCourseDetails,
  addSection,
  updateSection,
  deleteSection,
  addLesson,
  updateLesson,
  deleteLesson,
  updateSectionsOrder,
  publishCourse,
  unpublishCourse,
} from '../../services/courseService';
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import type { DragEndEvent } from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
  useSortable,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import Button from '../../components/ui/Button';
import Badge from '../../components/ui/Badge';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import SectionCard from '../../components/instructor/SectionCard';
import AddSectionForm from '../../components/instructor/AddSectionForm';
import EditSectionForm from '../../components/instructor/EditSectionForm';
import AddLessonForm from '../../components/instructor/AddLessonForm';
import EditLessonForm from '../../components/instructor/EditLessonForm';
import type { FullCourseDetails, Section, Lesson } from '../../types/api';

export default function ManageCoursePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<FullCourseDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Section modals state
  const [showAddSectionModal, setShowAddSectionModal] = useState(false);
  const [showEditSectionModal, setShowEditSectionModal] = useState(false);
  const [showDeleteSectionDialog, setShowDeleteSectionDialog] = useState(false);
  const [selectedSection, setSelectedSection] = useState<Section | null>(null);

  // Lesson modals state
  const [showAddLessonModal, setShowAddLessonModal] = useState(false);
  const [showEditLessonModal, setShowEditLessonModal] = useState(false);
  const [showDeleteLessonDialog, setShowDeleteLessonDialog] = useState(false);
  const [selectedLesson, setSelectedLesson] = useState<Lesson | null>(null);
  const [currentSection, setCurrentSection] = useState<Section | null>(null);

  // Loading states
  const [isAddingSection, setIsAddingSection] = useState(false);
  const [isUpdatingSection, setIsUpdatingSection] = useState(false);
  const [isDeletingSection, setIsDeletingSection] = useState(false);
  const [isAddingLesson, setIsAddingLesson] = useState(false);
  const [isUpdatingLesson, setIsUpdatingLesson] = useState(false);
  const [isDeletingLesson, setIsDeletingLesson] = useState(false);

  // Reorder mode state
  const [isReorderMode, setIsReorderMode] = useState(false);
  const [reorderedSections, setReorderedSections] = useState<Section[]>([]);
  const [isSavingOrder, setIsSavingOrder] = useState(false);

  // Publish/Unpublish state
  const [isPublishing, setIsPublishing] = useState(false);

  // Drag and drop sensors
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  useEffect(() => {
    const fetchCourse = async () => {
      if (!id) {
        setError('Course ID is missing');
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        const courseData = await getFullCourseDetails(parseInt(id));
        setCourse(courseData);
      } catch (err: any) {
        console.error('Error fetching course:', err);
        const errorMessage = err.response?.data?.message || 'Failed to load course';
        setError(errorMessage);

        if (err.response?.status === 403) {
          toast.error('You do not have permission to manage this course');
          setTimeout(() => navigate('/my-teaching'), 2000);
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchCourse();
  }, [id, navigate]);

  const canManage = user && (user.role === 'ADMIN' || user.role === 'INSTRUCTOR');

  useEffect(() => {
    if (!isLoading && !canManage && course) {
      toast.error('You must be an instructor or admin to manage courses');
      navigate('/my-teaching');
    }
  }, [isLoading, canManage, course, navigate]);

  const refreshCourse = async () => {
    if (!id) return;
    try {
      const courseData = await getFullCourseDetails(parseInt(id));
      setCourse(courseData);
    } catch (err: any) {
      console.error('Error refreshing course:', err);
      toast.error('Failed to refresh course data');
    }
  };

  const handleAddSection = async (title: string) => {
    if (!id || !course) return;

    try {
      setIsAddingSection(true);
      const orderIndex = course.sections.length;
      await addSection(parseInt(id), { title, orderIndex });
      toast.success('Section added successfully!');
      setShowAddSectionModal(false);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error adding section:', err);
      toast.error(err.response?.data?.message || 'Failed to add section');
    } finally {
      setIsAddingSection(false);
    }
  };

  const handleEditSection = async (title: string, orderIndex: number) => {
    if (!id || !selectedSection) return;

    try {
      setIsUpdatingSection(true);
      await updateSection(parseInt(id), selectedSection.id, { title, orderIndex });
      toast.success('Section updated successfully!');
      setShowEditSectionModal(false);
      setSelectedSection(null);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error updating section:', err);
      toast.error(err.response?.data?.message || 'Failed to update section');
    } finally {
      setIsUpdatingSection(false);
    }
  };

  const handleDeleteSection = async () => {
    if (!id || !selectedSection) return;

    try {
      setIsDeletingSection(true);
      await deleteSection(parseInt(id), selectedSection.id);
      toast.success('Section deleted successfully!');
      setShowDeleteSectionDialog(false);
      setSelectedSection(null);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error deleting section:', err);
      toast.error(err.response?.data?.message || 'Failed to delete section');
      setIsDeletingSection(false);
    }
  };


  const openEditModal = (section: Section) => {
    setSelectedSection(section);
    setShowEditSectionModal(true);
  };

  const openDeleteDialog = (section: Section) => {
    setSelectedSection(section);
    setShowDeleteSectionDialog(true);
  };

  // ============================================
  // Lesson Handlers
  // ============================================

  const handleAddLesson = async (data: any) => {
    if (!id || !currentSection) return;

    try {
      setIsAddingLesson(true);
      const orderIndex = currentSection.lessons.length;
      await addLesson(parseInt(id), currentSection.id, {
        ...data,
        orderIndex,
      });
      toast.success('Lesson added successfully!');
      setShowAddLessonModal(false);
      setCurrentSection(null);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error adding lesson:', err);
      toast.error(err.response?.data?.message || 'Failed to add lesson');
    } finally {
      setIsAddingLesson(false);
    }
  };

  const handleEditLesson = async (data: any) => {
    if (!id || !currentSection || !selectedLesson) return;

    try {
      setIsUpdatingLesson(true);
      await updateLesson(parseInt(id), currentSection.id, selectedLesson.id, data);
      toast.success('Lesson updated successfully!');
      setShowEditLessonModal(false);
      setSelectedLesson(null);
      setCurrentSection(null);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error updating lesson:', err);
      toast.error(err.response?.data?.message || 'Failed to update lesson');
    } finally {
      setIsUpdatingLesson(false);
    }
  };

  const handleDeleteLesson = async () => {
    if (!id || !currentSection || !selectedLesson) return;

    try {
      setIsDeletingLesson(true);
      await deleteLesson(parseInt(id), currentSection.id, selectedLesson.id);
      toast.success('Lesson deleted successfully!');
      setShowDeleteLessonDialog(false);
      setSelectedLesson(null);
      setCurrentSection(null);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error deleting lesson:', err);
      toast.error(err.response?.data?.message || 'Failed to delete lesson');
      setIsDeletingLesson(false);
    }
  };


  const openAddLessonModal = (section: Section) => {
    setCurrentSection(section);
    setShowAddLessonModal(true);
  };

  const openEditLessonModal = (section: Section, lesson: Lesson) => {
    setCurrentSection(section);
    setSelectedLesson(lesson);
    setShowEditLessonModal(true);
  };

  const openDeleteLessonDialog = (section: Section, lesson: Lesson) => {
    setCurrentSection(section);
    setSelectedLesson(lesson);
    setShowDeleteLessonDialog(true);
  };

  // Publish/Unpublish functions
  const handlePublish = async () => {
    if (!id || !course) return;

    // Walidacja: kurs musi mieć przynajmniej 1 sekcję z 1 lekcją
    if (course.sectionsCount === 0 || course.totalLessonsCount === 0) {
      toast.error('Cannot publish: Course must have at least 1 section with 1 lesson');
      return;
    }

    try {
      setIsPublishing(true);
      await publishCourse(parseInt(id));
      toast.success('Course published successfully!');
      await refreshCourse();
    } catch (err: any) {
      console.error('Error publishing course:', err);
      toast.error(err.response?.data?.message || 'Failed to publish course');
    } finally {
      setIsPublishing(false);
    }
  };

  const handleUnpublish = async () => {
    if (!id) return;

    try {
      setIsPublishing(true);
      await unpublishCourse(parseInt(id));
      toast.success('Course unpublished successfully!');
      await refreshCourse();
    } catch (err: any) {
      console.error('Error unpublishing course:', err);
      toast.error(err.response?.data?.message || 'Failed to unpublish course');
    } finally {
      setIsPublishing(false);
    }
  };

  // Reorder functions
  const handleEnterReorderMode = () => {
    if (course) {
      setReorderedSections([...course.sections]);
      setIsReorderMode(true);
    }
  };

  const handleCancelReorder = () => {
    setIsReorderMode(false);
    setReorderedSections([]);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      setReorderedSections((sections) => {
        const oldIndex = sections.findIndex((s) => s.id === active.id);
        const newIndex = sections.findIndex((s) => s.id === over.id);

        return arrayMove(sections, oldIndex, newIndex);
      });
    }
  };

  const handleSaveOrder = async () => {
    if (!id) return;

    try {
      setIsSavingOrder(true);

      // Create map of sectionId -> newOrderIndex
      const sectionOrderMap: Record<number, number> = {};
      reorderedSections.forEach((section, index) => {
        sectionOrderMap[section.id] = index;
      });

      await updateSectionsOrder(parseInt(id), sectionOrderMap);
      toast.success('Sections order updated successfully!');
      setIsReorderMode(false);
      await refreshCourse();
    } catch (err: any) {
      console.error('Error updating sections order:', err);
      toast.error(err.response?.data?.message || 'Failed to update sections order');
    } finally {
      setIsSavingOrder(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
          <p className="text-gray-400 mt-4">Loading course...</p>
        </div>
      </div>
    );
  }

  if (error && !course) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-400 text-lg">{error}</p>
          <Button variant="secondary" onClick={() => navigate('/my-teaching')} className="mt-4">
            Back to My Courses
          </Button>
        </div>
      </div>
    );
  }

  if (!course) return null;

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-start justify-between mb-4">
            <div>
              <h1 className="text-4xl font-bold text-gray-100 mb-2">{course.title}</h1>
              <p className="text-gray-400">Manage course content</p>
            </div>
            <div className="flex items-center gap-3">
              <Badge variant={course.published ? 'success' : 'warning'}>
                {course.published ? 'Published' : 'Unpublished'}
              </Badge>
              {course.published ? (
                <Button
                  variant="secondary"
                  onClick={handleUnpublish}
                  disabled={isPublishing}
                >
                  {isPublishing ? 'Unpublishing...' : 'Unpublish Course'}
                </Button>
              ) : (
                <Button
                  variant="primary"
                  onClick={handlePublish}
                  disabled={isPublishing || course.sectionsCount === 0 || course.totalLessonsCount === 0}
                >
                  {isPublishing ? 'Publishing...' : 'Publish Course'}
                </Button>
              )}
              <Button variant="secondary" onClick={() => navigate(`/courses/${id}/edit`)}>
                Edit Course
              </Button>
            </div>
          </div>

          {/* Publish Warning */}
          {!course.published && (course.sectionsCount === 0 || course.totalLessonsCount === 0) && (
            <div className="mt-6 bg-yellow-900/20 border border-yellow-700/50 rounded-xl p-4">
              <div className="flex items-start gap-3">
                <div className="flex-shrink-0">
                  <svg
                    className="w-5 h-5 text-yellow-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
                      clipRule="evenodd"
                    />
                  </svg>
                </div>
                <div>
                  <h3 className="text-yellow-500 font-semibold">Cannot Publish Course</h3>
                  <p className="text-gray-300 text-sm mt-1">
                    Your course must have at least 1 section with 1 lesson before it can be published.
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* Course Stats */}
          <div className="grid grid-cols-3 gap-4 mt-6">
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-4">
              <p className="text-gray-400 text-sm">Sections</p>
              <p className="text-2xl font-bold text-gray-100">{course.sectionsCount}</p>
            </div>
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-4">
              <p className="text-gray-400 text-sm">Total Lessons</p>
              <p className="text-2xl font-bold text-gray-100">{course.totalLessonsCount}</p>
            </div>
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-4">
              <p className="text-gray-400 text-sm">Price</p>
              <p className="text-2xl font-bold text-gray-100">
                {course.price} {course.currency}
              </p>
            </div>
          </div>
        </div>

        {/* Sections List */}
        <div className="space-y-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-gray-100">Course Content</h2>
            <div className="flex gap-2">
              {!isReorderMode ? (
                <>
                  {course.sections.length > 0 && (
                    <Button variant="secondary" onClick={handleEnterReorderMode}>
                      Reorder Sections
                    </Button>
                  )}
                  <Button variant="primary" onClick={() => setShowAddSectionModal(true)}>
                    + Add Section
                  </Button>
                </>
              ) : (
                <>
                  <Button variant="secondary" onClick={handleCancelReorder} disabled={isSavingOrder}>
                    Cancel
                  </Button>
                  <Button variant="primary" onClick={handleSaveOrder} disabled={isSavingOrder}>
                    {isSavingOrder ? 'Saving...' : 'Save Order'}
                  </Button>
                </>
              )}
            </div>
          </div>

          {course.sections.length === 0 ? (
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center">
              <p className="text-gray-400 text-lg mb-4">No sections yet</p>
              <p className="text-gray-500 mb-6">
                Start building your course by adding sections and lessons
              </p>
              <Button variant="primary" onClick={() => setShowAddSectionModal(true)}>
                Add Your First Section
              </Button>
            </div>
          ) : isReorderMode ? (
            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
              <SortableContext items={reorderedSections.map(s => s.id)} strategy={verticalListSortingStrategy}>
                {reorderedSections.map((section) => (
                  <SortableSectionCard
                    key={section.id}
                    section={section}
                  />
                ))}
              </SortableContext>
            </DndContext>
          ) : (
            course.sections.map((section, index) => (
              <SectionCard
                key={section.id}
                section={section}
                index={index}
                courseId={parseInt(id!)}
                onEdit={openEditModal}
                onDelete={openDeleteDialog}
                onAddLesson={openAddLessonModal}
                onEditLesson={openEditLessonModal}
                onDeleteLesson={openDeleteLessonDialog}
                onRefresh={refreshCourse}
              />
            ))
          )}
        </div>
      </div>

      {/* Add Section Modal */}
      <Modal
        isOpen={showAddSectionModal}
        onClose={() => setShowAddSectionModal(false)}
        title="Add New Section"
        size="md"
      >
        <AddSectionForm
          onSubmit={handleAddSection}
          onCancel={() => setShowAddSectionModal(false)}
          isLoading={isAddingSection}
        />
      </Modal>

      {/* Edit Section Modal */}
      {selectedSection && (
        <Modal
          isOpen={showEditSectionModal}
          onClose={() => {
            setShowEditSectionModal(false);
            setSelectedSection(null);
          }}
          title="Edit Section"
          size="md"
        >
          <EditSectionForm
            section={selectedSection}
            onSubmit={handleEditSection}
            onCancel={() => {
              setShowEditSectionModal(false);
              setSelectedSection(null);
            }}
            isLoading={isUpdatingSection}
          />
        </Modal>
      )}

      {/* Delete Section Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showDeleteSectionDialog}
        onClose={() => {
          setShowDeleteSectionDialog(false);
          setSelectedSection(null);
        }}
        onConfirm={handleDeleteSection}
        title="Delete Section"
        message={`Are you sure you want to delete "${selectedSection?.title}"? This will also delete all lessons in this section. This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeletingSection}
      />

      {/* Add Lesson Modal */}
      {currentSection && (
        <Modal
          isOpen={showAddLessonModal}
          onClose={() => {
            setShowAddLessonModal(false);
            setCurrentSection(null);
          }}
          title={`Add Lesson to "${currentSection.title}"`}
          size="lg"
        >
          <AddLessonForm
            onSubmit={handleAddLesson}
            onCancel={() => {
              setShowAddLessonModal(false);
              setCurrentSection(null);
            }}
            isLoading={isAddingLesson}
          />
        </Modal>
      )}

      {/* Edit Lesson Modal */}
      {selectedLesson && currentSection && (
        <Modal
          isOpen={showEditLessonModal}
          onClose={() => {
            setShowEditLessonModal(false);
            setSelectedLesson(null);
            setCurrentSection(null);
          }}
          title="Edit Lesson"
          size="lg"
        >
          <EditLessonForm
            lesson={selectedLesson}
            onSubmit={handleEditLesson}
            onCancel={() => {
              setShowEditLessonModal(false);
              setSelectedLesson(null);
              setCurrentSection(null);
            }}
            isLoading={isUpdatingLesson}
          />
        </Modal>
      )}

      {/* Delete Lesson Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showDeleteLessonDialog}
        onClose={() => {
          setShowDeleteLessonDialog(false);
          setSelectedLesson(null);
          setCurrentSection(null);
        }}
        onConfirm={handleDeleteLesson}
        title="Delete Lesson"
        message={`Are you sure you want to delete "${selectedLesson?.title}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeletingLesson}
      />
    </div>
  );
}

// Sortable Section Card Component for drag and drop
interface SortableSectionCardProps {
  section: Section;
}

function SortableSectionCard({
  section,
}: SortableSectionCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: section.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} className="bg-gray-800 border border-gray-700 rounded-xl p-6">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-3">
          <div {...attributes} {...listeners} className="cursor-grab active:cursor-grabbing">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-6 w-6 text-gray-400 hover:text-gray-300"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 6h16M4 12h16M4 18h16"
              />
            </svg>
          </div>
          <div>
            <h3 className="text-xl font-semibold text-gray-100">{section.title}</h3>
            <p className="text-sm text-gray-400">
              {section.lessons.length} lesson{section.lessons.length !== 1 ? 's' : ''}
            </p>
          </div>
        </div>
      </div>

      {/* Show lessons preview in reorder mode */}
      {section.lessons.length > 0 && (
        <div className="mt-4 space-y-2">
          {section.lessons.map((lesson) => (
            <div key={lesson.id} className="bg-gray-900 rounded-lg p-3">
              <p className="text-gray-200 text-sm">{lesson.title}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
