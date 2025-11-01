import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller, type SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import toast from 'react-hot-toast';
import { createCourse, getAllCategories } from '../../services/courseService';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Autocomplete from '../../components/ui/Autocomplete';

const createCourseSchema = z.object({
  title: z.string()
    .min(5, 'Title must be at least 5 characters')
    .max(200, 'Title must not exceed 200 characters'),
  description: z.string()
    .min(10, 'Description must be at least 10 characters')
    .max(5000, 'Description must not exceed 5000 characters'),
  price: z.string()
    .min(1, 'Price is required'),
  currency: z.string(),
  category: z.string()
    .min(1, 'Category is required')
    .max(100, 'Category must not exceed 100 characters'),
  level: z.enum(['BEGINNER', 'INTERMEDIATE', 'ADVANCED']),
  thumbnailUrl: z.string().optional(),
});

type CreateCourseFormData = z.infer<typeof createCourseSchema>;

export default function CreateCoursePage() {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categories, setCategories] = useState<string[]>([]);

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<CreateCourseFormData>({
    resolver: zodResolver(createCourseSchema),
    defaultValues: {
      currency: 'PLN',
      price: '0',
      category: '',
    },
  });

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await getAllCategories();
        setCategories(data);
      } catch (err) {
        console.error('Failed to fetch categories:', err);
      }
    };
    fetchCategories();
  }, []);

  const onSubmit: SubmitHandler<CreateCourseFormData> = async (data) => {
    try {
      setIsSubmitting(true);
      setError(null);

      const price = parseFloat(data.price);
      if (isNaN(price) || price < 0) {
        setError('Please enter a valid price');
        setIsSubmitting(false);
        return;
      }

      const courseData = {
        title: data.title,
        description: data.description,
        price,
        currency: data.currency,
        category: data.category,
        level: data.level,
        thumbnailUrl: data.thumbnailUrl || undefined,
      };

      const response = await createCourse(courseData);

      toast.success(response.message || 'Course created successfully!');

      if (response.resourceId) {
        navigate(`/dashboard/course/${response.resourceId}/manage`);
      } else {
        navigate('/my-teaching');
      }
    } catch (err: any) {
      console.error('Error creating course:', err);
      setError(err.response?.data?.message || 'Failed to create course. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-100 mb-2">Create New Course</h1>
          <p className="text-gray-400">Fill in the details to create your course</p>
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
                  <option value="">Select level...</option>
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

          <div className="flex gap-4">
            <Button
              type="button"
              variant="secondary"
              onClick={() => navigate('/my-teaching')}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              isLoading={isSubmitting}
              disabled={isSubmitting}
            >
              Create Course
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
