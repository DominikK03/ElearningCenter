import { Link } from 'react-router-dom';
import { BookOpen, Clock, User } from 'lucide-react';
import type { PublicCourse } from '../../types/api';
import { Card, CardContent, CardFooter } from '../ui/Card';
import Badge from '../ui/Badge';
import Button from '../ui/Button';

interface CourseCardProps {
  course: PublicCourse;
}

export default function CourseCard({ course }: CourseCardProps) {
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

  const totalDuration = 0;

  const formatDuration = (minutes: number) => {
    if (minutes < 60) return `${minutes}m`;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`;
  };

  return (
    <Card className="flex flex-col h-full hover:border-purple-600 transition-all duration-200 hover:shadow-purple-900/20 hover:shadow-2xl">
      <div className="relative w-full h-48 bg-gradient-to-br from-purple-900/40 to-pink-900/40 overflow-hidden">
        {course.thumbnailUrl ? (
          <img
            src={course.thumbnailUrl}
            alt={course.title}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <BookOpen className="w-16 h-16 text-purple-400 opacity-50" />
          </div>
        )}
        <div className="absolute top-3 left-3 flex gap-2">
          <Badge variant={levelColors[course.level]}>{levelLabels[course.level]}</Badge>
          <Badge variant="purple">{course.category}</Badge>
        </div>
      </div>

      <CardContent className="flex-1 flex flex-col gap-3">
        <h3 className="text-xl font-bold text-gray-100 line-clamp-2 hover:text-purple-400 transition-colors">
          {course.title}
        </h3>

        <p className="text-sm text-gray-400 line-clamp-3 flex-1">{course.description}</p>

        <div className="flex items-center gap-4 text-xs text-gray-500">
          <div className="flex items-center gap-1">
            <BookOpen className="w-4 h-4" />
            <span>
              {course.sectionsCount} sections • {course.totalLessonsCount} lessons
            </span>
          </div>
          {totalDuration > 0 && (
            <div className="flex items-center gap-1">
              <Clock className="w-4 h-4" />
              <span>{formatDuration(totalDuration)}</span>
            </div>
          )}
        </div>

        <div className="flex items-center gap-2 text-sm text-gray-400">
          <User className="w-4 h-4" />
          <span>by {course.instructorName}</span>
        </div>
      </CardContent>

      <CardFooter className="flex items-center justify-between">
        <div className="flex flex-col">
          <span className="text-2xl font-bold text-purple-400">
            {course.price} {course.currency}
          </span>
        </div>
        <Link to={`/courses/${course.id}`}>
          <Button variant="primary">View Details</Button>
        </Link>
      </CardFooter>
    </Card>
  );
}
