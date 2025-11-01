import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import Button from '../ui/Button';
import Input from '../ui/Input';

const addLessonSchema = z.object({
  title: z.string()
    .min(3, 'Title must be at least 3 characters')
    .max(200, 'Title must not exceed 200 characters'),
  content: z.string().optional(),
  videoUrl: z.string().url('Must be a valid URL').optional().or(z.literal('')),
  durationMinutes: z.number().int().min(1, 'Duration must be at least 1 minute').optional(),
});

type AddLessonFormData = z.infer<typeof addLessonSchema>;

interface AddLessonFormProps {
  onSubmit: (data: AddLessonFormData) => Promise<void>;
  onCancel: () => void;
  isLoading?: boolean;
}

export default function AddLessonForm({ onSubmit, onCancel, isLoading }: AddLessonFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<AddLessonFormData>({
    resolver: zodResolver(addLessonSchema),
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <Input
        label="Lesson Title"
        {...register('title')}
        error={errors.title?.message}
        placeholder="e.g., Introduction to Variables"
        required
      />

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Content (Optional)
        </label>
        <textarea
          {...register('content')}
          className="w-full px-4 py-3 bg-gray-800 border border-gray-700 rounded-lg text-gray-100
                     placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500
                     focus:border-transparent transition-all min-h-[150px]"
          placeholder="Lesson content, notes, or description..."
        />
        {errors.content && (
          <p className="text-red-400 text-sm mt-1">{errors.content.message}</p>
        )}
      </div>

      <Input
        label="Video URL (Optional)"
        {...register('videoUrl')}
        error={errors.videoUrl?.message}
        placeholder="https://youtube.com/watch?v=..."
        type="url"
      />

      <div>
        <label className="block text-sm font-medium text-gray-300 mb-2">
          Duration (minutes) (Optional)
        </label>
        <input
          type="number"
          {...register('durationMinutes', {
            setValueAs: (v) => v === '' ? undefined : parseInt(v, 10)
          })}
          className="w-full px-4 py-3 bg-gray-800 border border-gray-700 rounded-lg text-gray-100
                     placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500
                     focus:border-transparent transition-all"
          placeholder="e.g., 15"
          min="1"
        />
        {errors.durationMinutes && (
          <p className="text-red-400 text-sm mt-1">{errors.durationMinutes.message}</p>
        )}
      </div>

      <div className="flex gap-3 pt-4">
        <Button
          type="submit"
          variant="primary"
          disabled={isLoading}
          className="flex-1"
        >
          {isLoading ? 'Adding...' : 'Add Lesson'}
        </Button>
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          disabled={isLoading}
          className="flex-1"
        >
          Cancel
        </Button>
      </div>
    </form>
  );
}
