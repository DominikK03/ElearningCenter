import { X } from 'lucide-react';
import Button from '../ui/Button';
import type { User, PublicCourseDetails } from '../../types/api';

interface EnrollmentConfirmDialogProps {
  course: PublicCourseDetails;
  user: User;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  isEnrolling: boolean;
}

export default function EnrollmentConfirmDialog({
  course,
  user,
  isOpen,
  onClose,
  onConfirm,
  isEnrolling,
}: EnrollmentConfirmDialogProps) {
  if (!isOpen) return null;

  const hasEnoughBalance = user.balance >= course.price;
  const balanceAfterEnrollment = user.balance - course.price;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
      <div className="bg-gray-800 rounded-xl shadow-xl max-w-md w-full border border-gray-700">
        <div className="flex items-center justify-between p-6 border-b border-gray-700">
          <h2 className="text-xl font-semibold text-gray-100">
            Confirm Enrollment
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-100 transition-colors"
            disabled={isEnrolling}
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-4">
          <p className="text-gray-300">
            Enroll in <span className="font-semibold text-gray-100">{course.title}</span>?
          </p>

          <div className="bg-gray-900 rounded-lg p-4 space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-gray-400">Price:</span>
              <span className="font-semibold text-gray-100">{course.price.toFixed(2)} PLN</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-400">Your balance:</span>
              <span className={`font-semibold ${hasEnoughBalance ? 'text-green-400' : 'text-red-400'}`}>
                {user.balance.toFixed(2)} PLN
              </span>
            </div>
            {hasEnoughBalance && (
              <div className="flex justify-between text-sm pt-2 border-t border-gray-700">
                <span className="text-gray-400">After enrollment:</span>
                <span className="font-semibold text-gray-100">
                  {balanceAfterEnrollment.toFixed(2)} PLN
                </span>
              </div>
            )}
          </div>

          {!hasEnoughBalance && (
            <div className="bg-red-900/20 border border-red-700/50 rounded-lg p-4">
              <p className="text-red-400 text-sm">
                Insufficient balance. You need {(course.price - user.balance).toFixed(2)} PLN more.
              </p>
            </div>
          )}
        </div>

        <div className="flex gap-3 p-6 border-t border-gray-700">
          <Button
            variant="secondary"
            onClick={onClose}
            disabled={isEnrolling}
            className="flex-1"
          >
            Cancel
          </Button>
          <Button
            variant="primary"
            onClick={onConfirm}
            disabled={!hasEnoughBalance || isEnrolling}
            className="flex-1"
          >
            {isEnrolling ? 'Enrolling...' : 'Confirm Enrollment'}
          </Button>
        </div>
      </div>
    </div>
  );
}
