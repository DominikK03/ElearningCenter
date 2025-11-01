import { ChevronDown, ChevronUp, Edit, Trash2 } from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
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
import Button from '../ui/Button';
import LessonCard from './LessonCard';
import { updateLessonsOrder } from '../../services/courseService';
import type { Section, Lesson } from '../../types/api';

interface SectionCardProps {
  section: Section;
  index: number;
  courseId: number;
  onEdit: (section: Section) => void;
  onDelete: (section: Section) => void;
  onAddLesson: (section: Section) => void;
  onEditLesson: (section: Section, lesson: Lesson) => void;
  onDeleteLesson: (section: Section, lesson: Lesson) => void;
  onRefresh: () => Promise<void>;
}

export default function SectionCard({
  section,
  index,
  courseId,
  onEdit,
  onDelete,
  onAddLesson,
  onEditLesson,
  onDeleteLesson,
  onRefresh,
}: SectionCardProps) {
  const [isExpanded, setIsExpanded] = useState(true);
  const [isReorderMode, setIsReorderMode] = useState(false);
  const [reorderedLessons, setReorderedLessons] = useState<Lesson[]>([]);
  const [isSavingOrder, setIsSavingOrder] = useState(false);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleEnterReorderMode = () => {
    setReorderedLessons([...section.lessons]);
    setIsReorderMode(true);
  };

  const handleCancelReorder = () => {
    setIsReorderMode(false);
    setReorderedLessons([]);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      setReorderedLessons((lessons) => {
        const oldIndex = lessons.findIndex((l) => l.id === active.id);
        const newIndex = lessons.findIndex((l) => l.id === over.id);

        return arrayMove(lessons, oldIndex, newIndex);
      });
    }
  };

  const handleSaveOrder = async () => {
    try {
      setIsSavingOrder(true);

      const lessonOrderMap: Record<number, number> = {};
      reorderedLessons.forEach((lesson, idx) => {
        lessonOrderMap[lesson.id] = idx;
      });

      await updateLessonsOrder(courseId, section.id, lessonOrderMap);
      toast.success('Lessons order updated successfully!');
      setIsReorderMode(false);
      await onRefresh();
    } catch (err: any) {
      console.error('Error updating lessons order:', err);
      toast.error(err.response?.data?.message || 'Failed to update lessons order');
    } finally {
      setIsSavingOrder(false);
    }
  };

  return (
    <div className="bg-gray-800 border border-gray-700 rounded-xl overflow-hidden">
      {/* Section Header */}
      <div className="p-6">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-3">
              <button
                onClick={() => setIsExpanded(!isExpanded)}
                className="text-gray-400 hover:text-gray-100 transition-colors"
              >
                {isExpanded ? (
                  <ChevronDown className="w-5 h-5" />
                ) : (
                  <ChevronUp className="w-5 h-5" />
                )}
              </button>
              <h3 className="text-xl font-semibold text-gray-100">
                Section {index + 1}: {section.title}
              </h3>
            </div>
            <p className="text-sm text-gray-400 ml-8 mt-1">
              {section.lessons.length} {section.lessons.length === 1 ? 'lesson' : 'lessons'}
            </p>
          </div>

          <div className="flex items-center gap-2">
            {/* Edit/Delete Buttons */}
            <Button
              variant="ghost"
              onClick={() => onEdit(section)}
              className="!p-2"
              title="Edit section"
            >
              <Edit className="w-4 h-4" />
            </Button>
            <Button
              variant="ghost"
              onClick={() => onDelete(section)}
              className="!p-2 !text-red-400 hover:!text-red-300"
              title="Delete section"
            >
              <Trash2 className="w-4 h-4" />
            </Button>
          </div>
        </div>
      </div>

      {/* Lessons List */}
      {isExpanded && (
        <div className="border-t border-gray-700 bg-gray-900/50 p-6">
          {section.lessons.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-400 mb-4">No lessons yet</p>
              <Button variant="primary" onClick={() => onAddLesson(section)}>
                + Add Lesson
              </Button>
            </div>
          ) : (
            <>
              {isReorderMode && (
                <div className="mb-4 flex justify-end gap-2">
                  <Button variant="secondary" onClick={handleCancelReorder} disabled={isSavingOrder} className="text-sm">
                    Cancel
                  </Button>
                  <Button variant="primary" onClick={handleSaveOrder} disabled={isSavingOrder} className="text-sm">
                    {isSavingOrder ? 'Saving...' : 'Save Order'}
                  </Button>
                </div>
              )}

              <div className="space-y-3">
                {isReorderMode ? (
                  <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                    <SortableContext items={reorderedLessons.map(l => l.id)} strategy={verticalListSortingStrategy}>
                      {reorderedLessons.map((lesson) => (
                        <SortableLessonCard key={lesson.id} lesson={lesson} />
                      ))}
                    </SortableContext>
                  </DndContext>
                ) : (
                  <>
                    {section.lessons.map((lesson, lessonIndex) => (
                      <LessonCard
                        key={lesson.id}
                        lesson={lesson}
                        index={lessonIndex}
                        onEdit={(lesson) => onEditLesson(section, lesson)}
                        onDelete={(lesson) => onDeleteLesson(section, lesson)}
                      />
                    ))}
                  </>
                )}

                {!isReorderMode && (
                  <div className="pt-2 flex gap-2">
                    <Button variant="secondary" onClick={() => onAddLesson(section)}>
                      + Add Lesson
                    </Button>
                    {section.lessons.length > 0 && (
                      <Button variant="secondary" onClick={handleEnterReorderMode} className="text-sm">
                        Reorder Lessons
                      </Button>
                    )}
                  </div>
                )}
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}

// Sortable Lesson Card Component for drag and drop
interface SortableLessonCardProps {
  lesson: Lesson;
}

function SortableLessonCard({ lesson }: SortableLessonCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: lesson.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="flex items-center gap-3 p-4 bg-gray-800 rounded-lg border border-gray-700"
    >
      <div {...attributes} {...listeners} className="cursor-grab active:cursor-grabbing">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-5 w-5 text-gray-400 hover:text-gray-300"
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
      <div className="flex-1">
        <h4 className="text-gray-100 font-medium">{lesson.title}</h4>
        {lesson.durationMinutes && (
          <p className="text-sm text-gray-400">{lesson.durationMinutes} min</p>
        )}
      </div>
    </div>
  );
}
