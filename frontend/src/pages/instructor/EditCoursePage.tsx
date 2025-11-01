import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm, Controller, type SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import {
  getCourseById,
  updateCourse,
  deleteCourse,
  publishCourse,
  unpublishCourse,
  getAllCategories,
} from '../../services/courseService';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Autocomplete from '../../components/ui/Autocomplete';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import Badge from '../../components/ui/Badge';
import type { PublicCourseDetails, CourseLevel } from '../../types/api';

const updateCourseSchema = z.object({
  title: z
    .string()
    .min(5, 'Title must be at least 5 characters')
    .max(200, 'Title must not exceed 200 characters'),
  description: z
    .string()
    .min(10, 'Description must be at least 10 characters')
    .max(5000, 'Description must not exceed 5000 characters'),
  price: z.string().min(1, 'Price is required'),
  currency: z.string(),
  category: z
    .string()
    .min(1, 'Category is required')
    .max(100, 'Category must not exceed 100 characters'),
  level: z.enum(['BEGINNER', 'INTERMEDIATE', 'ADVANCED']),
  thumbnailUrl: z.string().optional(),
});

type UpdateCourseFormData = z.infer<typeof updateCourseSchema>;

export default function EditCoursePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<PublicCourseDetails | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categories, setCategories] = useState<string[]>([]);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors },
  } = useForm<UpdateCourseFormData>({
    resolver: zodResolver(updateCourseSchema),
  });

  useEffect(() => {
    const fetchCourse = async () => {
      if (!id) {
        setError('Course ID is missing');
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        const [courseData, categoriesData] = await Promise.all([
          getCourseById(parseInt(id)),
          getAllCategories(),
        ]);

        setCourse(courseData);
        setCategories(categoriesData);

        // Pre-fill form
        reset({
          title: courseData.title,
          description: courseData.description,
          price: courseData.price.toString(),
          currency: courseData.currency,
          category: courseData.category,
          level: courseData.level as CourseLevel,
          thumbnailUrl: courseData.thumbnailUrl || '',
        });
      } catch (err: any) {
        console.error('Error fetching course:', err);
        setError(err.response?.data?.message || 'Failed to load course');
      } finally {
        setIsLoading(false);
      }
    };

    fetchCourse();
  }, [id, reset]);

  // Authorization check - backend will handle this, but we check if user is instructor or admin
  const canEdit = user && (user.role === 'ADMIN' || user.role === 'INSTRUCTOR');

  useEffect(() => {
    if (!isLoading && !canEdit && course) {
      toast.error('You must be an instructor or admin to edit courses');
      navigate('/my-teaching');
    }
  }, [isLoading, canEdit, course, navigate]);

  const onSubmit: SubmitHandler<UpdateCourseFormData> = async (data) => {
    if (!id || !course) return;

    try {
      setIsSubmitting(true);
      setError(null);

      const price = parseFloat(data.price);
      if (isNaN(price) || price < 0) {
        setError('Please enter a valid price');
        setIsSubmitting(false);
        return;
      }

      const updateData = {
        title: data.title,
        description: data.description,
        price,
        currency: data.currency,
        category: data.category,
        level: data.level,
        thumbnailUrl: data.thumbnailUrl || undefined,
      };

      await updateCourse(parseInt(id), updateData);
      toast.success('Course updated successfully!');
      navigate('/my-teaching');
    } catch (err: any) {
      console.error('Error updating course:', err);
      const errorMessage = err.response?.data?.message || 'Failed to update course. Please try again.';
      setError(errorMessage);
      if (err.response?.status === 403) {
        toast.error('You do not have permission to edit this course');
        setTimeout(() => navigate('/my-teaching'), 2000);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!id) return;

    try {
      setIsDeleting(true);
      await deleteCourse(parseInt(id));
      toast.success('Course deleted successfully!');
      navigate('/my-teaching');
    } catch (err: any) {
      console.error('Error deleting course:', err);
      const errorMessage = err.response?.data?.message || 'Failed to delete course';
      toast.error(errorMessage);
      if (err.response?.status === 403) {
        toast.error('You do not have permission to delete this course');
      }
      setIsDeleting(false);
      setShowDeleteDialog(false);
    }
  };

  const handlePublishToggle = async () => {
    if (!id || !course) return;

    try {
      setIsPublishing(true);

      if (course.published) {
        await unpublishCourse(parseInt(id));
        toast.success('Course unpublished successfully!');
        setCourse({ ...course, published: false });
      } else {
        await publishCourse(parseInt(id));
        toast.success('Course published successfully!');
        setCourse({ ...course, published: true });
      }
    } catch (err: any) {
      console.error('Error toggling publish status:', err);
      const errorMessage = err.response?.data?.message || 'Failed to update publish status';
      toast.error(errorMessage);
      if (err.response?.status === 403) {
        toast.error('You do not have permission to publish/unpublish this course');
      }
    } finally {
      setIsPublishing(false);
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
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8 flex items-start justify-between">
          <div>
            <h1 className="text-4xl font-bold text-gray-100 mb-2">Edit Course</h1>
            <p className="text-gray-400">Update your course details</p>
          </div>
          <div className="flex items-center gap-3">
            <Badge variant={course.published ? 'success' : 'warning'}>
              {course.published ? 'Published' : 'Unpublished'}
            </Badge>
          </div>
        </div>

        {error && (
          <div className="bg-red-900/30 border border-red-700 text-red-400 px-6 py-4 rounded-xl mb-6">
            <p className="font-medium">Error</p>
            <p className="text-sm mt-1">{error}</p>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 space-y-6">
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-300 mb-2">
                Course Title *
              </label>
              <Input
                id="title"
                type="text"
                placeholder="e.g., Complete Spring Boot Course"
                {...register('title')}
                className={errors.title ? 'border-red-500' : ''}
              />
              {errors.title && (
                <p className="mt-1 text-sm text-red-400">{errors.title.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-300 mb-2">
                Description *
              </label>
              <textarea
                id="description"
                rows={6}
                placeholder="Describe what students will learn in this course..."
                {...register('description')}
                className={`w-full px-4 py-3 bg-gray-900 border rounded-xl text-gray-100 placeholder-gray-500
                         focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent
                         transition-all duration-200 ${errors.description ? 'border-red-500' : 'border-gray-700'}`}
              />
              {errors.description && (
                <p className="mt-1 text-sm text-red-400">{errors.description.message}</p>
              )}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="price" className="block text-sm font-medium text-gray-300 mb-2">
                  Price (PLN) *
                </label>
                <Input
                  id="price"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="0.00"
                  {...register('price')}
                  className={errors.price ? 'border-red-500' : ''}
                />
                {errors.price && (
                  <p className="mt-1 text-sm text-red-400">{errors.price.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="level" className="block text-sm font-medium text-gray-300 mb-2">
                  Level *
                </label>
                <select
                  id="level"
                  {...register('level')}
                  className={`w-full px-4 py-3 bg-gray-900 border rounded-xl text-gray-100
                           focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent
                           transition-all duration-200 ${errors.level ? 'border-red-500' : 'border-gray-700'}`}
                >
                  <option value="BEGINNER">Beginner</option>
                  <option value="INTERMEDIATE">Intermediate</option>
                  <option value="ADVANCED">Advanced</option>
                </select>
                {errors.level && (
                  <p className="mt-1 text-sm text-red-400">{errors.level.message}</p>
                )}
              </div>
            </div>

            <div>
              <label htmlFor="category" className="block text-sm font-medium text-gray-300 mb-2">
                Category *
              </label>
              <Controller
                name="category"
                control={control}
                render={({ field }) => (
                  <Autocomplete
                    value={field.value}
                    onChange={field.onChange}
                    options={categories}
                    placeholder="Select or type a category..."
                    className={errors.category ? 'border-red-500' : ''}
                  />
                )}
              />
              {errors.category && (
                <p className="mt-1 text-sm text-red-400">{errors.category.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="thumbnailUrl" className="block text-sm font-medium text-gray-300 mb-2">
                Thumbnail URL (Optional)
              </label>
              <Input
                id="thumbnailUrl"
                type="url"
                placeholder="https://example.com/image.jpg"
                {...register('thumbnailUrl')}
                className={errors.thumbnailUrl ? 'border-red-500' : ''}
              />
              {errors.thumbnailUrl && (
                <p className="mt-1 text-sm text-red-400">{errors.thumbnailUrl.message}</p>
              )}
              <p className="mt-1 text-sm text-gray-500">
                Provide a URL to an image for your course thumbnail
              </p>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex gap-4 flex-1">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate('/my-teaching')}
                disabled={isSubmitting || isDeleting || isPublishing}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                isLoading={isSubmitting}
                disabled={isSubmitting || isDeleting || isPublishing}
              >
                Update Course
              </Button>
            </div>

            <div className="flex gap-4">
              <Button
                type="button"
                variant="ghost"
                onClick={handlePublishToggle}
                isLoading={isPublishing}
                disabled={isSubmitting || isDeleting || isPublishing}
              >
                {course.published ? 'Unpublish' : 'Publish'}
              </Button>
              <Button
                type="button"
                variant="danger"
                onClick={() => setShowDeleteDialog(true)}
                disabled={isSubmitting || isDeleting || isPublishing}
              >
                Delete Course
              </Button>
            </div>
          </div>
        </form>
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showDeleteDialog}
        onClose={() => setShowDeleteDialog(false)}
        onConfirm={handleDelete}
        title="Delete Course"
        message={`Are you sure you want to delete "${course.title}"? This action cannot be undone and will remove all sections, lessons, and enrollments.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  );
}
