import { useForm, type SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import Button from '../ui/Button';
import Input from '../ui/Input';

const addSectionSchema = z.object({
  title: z.string()
    .min(3, 'Title must be at least 3 characters')
    .max(200, 'Title must not exceed 200 characters'),
});

type AddSectionFormData = z.infer<typeof addSectionSchema>;

interface AddSectionFormProps {
  onSubmit: (title: string) => Promise<void>;
  onCancel: () => void;
  isLoading: boolean;
}

export default function AddSectionForm({ onSubmit, onCancel, isLoading }: AddSectionFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<AddSectionFormData>({
    resolver: zodResolver(addSectionSchema),
  });

  const handleFormSubmit: SubmitHandler<AddSectionFormData> = async (data) => {
    await onSubmit(data.title);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div>
        <label htmlFor="title" className="block text-sm font-medium text-gray-300 mb-2">
          Section Title *
        </label>
        <Input
          id="title"
          type="text"
          placeholder="e.g., Introduction to Spring Boot"
          {...register('title')}
          className={errors.title ? 'border-red-500' : ''}
          disabled={isLoading}
        />
        {errors.title && (
          <p className="mt-1 text-sm text-red-400">{errors.title.message}</p>
        )}
      </div>

      <div className="flex gap-3 justify-end pt-2">
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          disabled={isLoading}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          variant="primary"
          isLoading={isLoading}
          disabled={isLoading}
        >
          Add Section
        </Button>
      </div>
    </form>
  );
}
