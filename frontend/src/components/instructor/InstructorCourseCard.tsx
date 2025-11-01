import { Eye, Settings, Trash2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Button from '../ui/Button';
import Badge from '../ui/Badge';
import { Card } from '../ui/Card';
import type { Course } from '../../types/api';

interface InstructorCourseCardProps {
  course: Course;
  onDelete: (course: Course) => void;
}

export default function InstructorCourseCard({ course, onDelete }: InstructorCourseCardProps) {
  const navigate = useNavigate();

  return (
    <Card className="overflow-hidden hover:border-purple-500/50 transition-all duration-200">
      <div className="flex flex-col h-full">
        {/* Thumbnail */}
        {course.thumbnailUrl && (
          <div className="aspect-video w-full overflow-hidden bg-gray-900">
            <img
              src={course.thumbnailUrl}
              alt={course.title}
              className="w-full h-full object-cover"
            />
          </div>
        )}

        {/* Content */}
        <div className="p-6 flex-1 flex flex-col">
          {/* Header with Status */}
          <div className="flex items-start justify-between mb-3">
            <h3 className="text-xl font-semibold text-gray-100 flex-1 line-clamp-2">
              {course.title}
            </h3>
            <Badge variant={course.published ? 'success' : 'warning'} className="ml-2 flex-shrink-0">
              {course.published ? 'Published' : 'Draft'}
            </Badge>
          </div>

          {/* Description */}
          <p className="text-gray-400 text-sm mb-4 line-clamp-2">
            {course.description}
          </p>

          {/* Stats */}
          <div className="flex items-center gap-4 text-sm text-gray-400 mb-4">
            <div className="flex items-center gap-1">
              <span className="font-medium">{course.sectionsCount}</span>
              <span>{course.sectionsCount === 1 ? 'Section' : 'Sections'}</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="font-medium">{course.totalLessonsCount}</span>
              <span>{course.totalLessonsCount === 1 ? 'Lesson' : 'Lessons'}</span>
            </div>
          </div>

          {/* Category and Level */}
          <div className="flex items-center gap-2 mb-4">
            <Badge variant="default">{course.category}</Badge>
            <Badge variant="purple">{course.level}</Badge>
          </div>

          {/* Price */}
          <div className="mb-4">
            <span className="text-2xl font-bold text-gray-100">
              {course.price} {course.currency}
            </span>
          </div>

          {/* Actions */}
          <div className="grid grid-cols-3 gap-2 mt-auto">
            <Button
              variant="primary"
              onClick={() => navigate(`/dashboard/course/${course.id}/manage`)}
              className="!p-3"
              title="Manage course content"
            >
              <Settings className="w-4 h-4" />
              <span className="hidden sm:inline">Manage</span>
            </Button>
            <Button
              variant="secondary"
              onClick={() => navigate(`/courses/${course.id}`)}
              className="!p-3"
              title="View as student"
            >
              <Eye className="w-4 h-4" />
              <span className="hidden sm:inline">View</span>
            </Button>
            <Button
              variant="danger"
              onClick={() => onDelete(course)}
              className="!p-3"
              title="Delete course"
            >
              <Trash2 className="w-4 h-4" />
              <span className="hidden sm:inline">Delete</span>
            </Button>
          </div>
        </div>
      </div>
    </Card>
  );
}
