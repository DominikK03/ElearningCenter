import { useForm, type SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import Button from '../ui/Button';
import Input from '../ui/Input';
import type { Section } from '../../types/api';

const editSectionSchema = z.object({
  title: z.string()
    .min(3, 'Title must be at least 3 characters')
    .max(200, 'Title must not exceed 200 characters'),
  orderIndex: z.number().int().min(0),
});

type EditSectionFormData = z.infer<typeof editSectionSchema>;

interface EditSectionFormProps {
  section: Section;
  onSubmit: (title: string, orderIndex: number) => Promise<void>;
  onCancel: () => void;
  isLoading: boolean;
}

export default function EditSectionForm({ section, onSubmit, onCancel, isLoading }: EditSectionFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EditSectionFormData>({
    resolver: zodResolver(editSectionSchema),
    defaultValues: {
      title: section.title,
      orderIndex: section.orderIndex,
    },
  });

  const handleFormSubmit: SubmitHandler<EditSectionFormData> = async (data) => {
    await onSubmit(data.title, data.orderIndex);
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

      <div>
        <label htmlFor="orderIndex" className="block text-sm font-medium text-gray-300 mb-2">
          Order Index
        </label>
        <Input
          id="orderIndex"
          type="number"
          {...register('orderIndex', { valueAsNumber: true })}
          className={errors.orderIndex ? 'border-red-500' : ''}
          disabled={isLoading}
        />
        {errors.orderIndex && (
          <p className="mt-1 text-sm text-red-400">{errors.orderIndex.message}</p>
        )}
        <p className="mt-1 text-sm text-gray-500">
          Lower numbers appear first
        </p>
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
          Update Section
        </Button>
      </div>
    </form>
  );
}
