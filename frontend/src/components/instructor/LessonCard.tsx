import { Edit, Trash2, Video, Clock } from 'lucide-react';
import Button from '../ui/Button';
import type { Lesson } from '../../types/api';

interface LessonCardProps {
  lesson: Lesson;
  index: number;
  onEdit: (lesson: Lesson) => void;
  onDelete: (lesson: Lesson) => void;
}

export default function LessonCard({
  lesson,
  index,
  onEdit,
  onDelete,
}: LessonCardProps) {
  return (
    <div className="flex items-center justify-between p-4 bg-gray-800 rounded-lg border border-gray-700 hover:border-gray-600 transition-colors">
      <div className="flex-1">
        <div className="flex items-center gap-3">
          <span className="text-gray-400 text-sm font-medium">
            {index + 1}.
          </span>
          <div className="flex-1">
            <h4 className="text-gray-100 font-medium mb-1">{lesson.title}</h4>

            <div className="flex items-center gap-4 text-sm text-gray-400">
              {lesson.durationMinutes && (
                <div className="flex items-center gap-1">
                  <Clock className="w-4 h-4" />
                  <span>{lesson.durationMinutes} min</span>
                </div>
              )}

              {lesson.videoUrl && (
                <div className="flex items-center gap-1">
                  <Video className="w-4 h-4" />
                  <span>Video</span>
                </div>
              )}

              {lesson.materials && lesson.materials.length > 0 && (
                <span>{lesson.materials.length} materials</span>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2">
        {/* Edit/Delete Buttons */}
        <Button
          variant="ghost"
          onClick={() => onEdit(lesson)}
          className="!p-2"
          title="Edit lesson"
        >
          <Edit className="w-4 h-4" />
        </Button>
        <Button
          variant="ghost"
          onClick={() => onDelete(lesson)}
          className="!p-2 !text-red-400 hover:!text-red-300"
          title="Delete lesson"
        >
          <Trash2 className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
}
