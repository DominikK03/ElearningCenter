import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { KeyRound, CheckCircle, XCircle } from 'lucide-react';

import { userService } from '../../services';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';
import PasswordStrength from '../../components/ui/PasswordStrength';

// Validation schema
const resetPasswordSchema = z.object({
  newPassword: z.string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
    .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .regex(/\d/, 'Password must contain at least one number'),
  confirmPassword: z.string()
    .min(1, 'Please confirm your password'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [token, setToken] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [countdown, setCountdown] = useState(5);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
  });

  const passwordValue = watch('newPassword');

  useEffect(() => {
    const urlToken = searchParams.get('token');
    if (!urlToken) {
      toast.error('Password reset token is missing from the URL.');
    }
    setToken(urlToken);
  }, [searchParams]);

  useEffect(() => {
    if (success && countdown > 0) {
      const timer = setTimeout(() => {
        setCountdown(countdown - 1);
      }, 1000);
      return () => clearTimeout(timer);
    } else if (success && countdown === 0) {
      navigate('/login');
    }
  }, [success, countdown, navigate]);

  const onSubmit = async (data: ResetPasswordFormData) => {
    if (!token) {
      toast.error('Invalid or missing reset token.');
      return;
    }

    try {
      await userService.resetPassword({
        token,
        newPassword: data.newPassword,
      });
      setSuccess(true);
      toast.success('Password reset successful!');
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to reset password. The token may be invalid or expired.';
      toast.error(message);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4">
        <div className="max-w-md w-full text-center space-y-6">
          <div className="flex justify-center">
            <div className="bg-gradient-to-br from-green-600 to-emerald-600 p-5 rounded-3xl shadow-2xl">
              <CheckCircle className="h-16 w-16 text-white" />
            </div>
          </div>
          <h2 className="text-4xl font-bold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent">
            Password Reset Successful!
          </h2>
          <div className="bg-gray-800/50 border border-green-500/30 rounded-2xl p-5 backdrop-blur-sm">
            <p className="text-sm text-gray-300">
              Your password has been successfully reset.
              You can now log in with your new password.
            </p>
          </div>
          <p className="text-gray-400">
            Redirecting to login in {countdown} second{countdown !== 1 ? 's' : ''}...
          </p>
          <Button
            onClick={() => navigate('/login')}
            variant="primary"
            className="w-full"
          >
            Go to Login Now
          </Button>
        </div>
      </div>
    );
  }

  if (!token) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4">
        <div className="max-w-md w-full text-center space-y-6">
          <div className="flex justify-center">
            <div className="bg-gradient-to-br from-red-600 to-orange-600 p-5 rounded-3xl shadow-2xl">
              <XCircle className="h-16 w-16 text-white" />
            </div>
          </div>
          <h2 className="text-3xl font-bold bg-gradient-to-r from-red-400 to-orange-400 bg-clip-text text-transparent">
            Invalid Reset Link
          </h2>
          <div className="bg-gray-800/50 border border-red-500/30 rounded-2xl p-5 backdrop-blur-sm">
            <p className="text-sm text-gray-300">
              The password reset link is invalid or missing.
              Please request a new password reset link.
            </p>
          </div>
          <div className="space-y-3">
            <Link to="/request-password-reset">
              <Button variant="primary" className="w-full">
                Request New Reset Link
              </Button>
            </Link>
            <Link to="/login">
              <Button variant="secondary" className="w-full">
                Back to Login
              </Button>
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="flex justify-center">
            <div className="bg-gradient-to-br from-purple-600 to-pink-600 p-4 rounded-2xl shadow-lg">
              <KeyRound className="h-10 w-10 text-white" />
            </div>
          </div>
          <h2 className="mt-8 text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
            Set New Password
          </h2>
          <p className="mt-3 text-sm text-gray-400">
            Enter your new password below.
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          <div className="space-y-4">
            <div>
              <Input
                label="New Password"
                type="password"
                autoComplete="new-password"
                placeholder="••••••••"
                required
                error={errors.newPassword?.message}
                {...register('newPassword')}
              />
              <PasswordStrength password={passwordValue || ''} />
            </div>

            <Input
              label="Confirm New Password"
              type="password"
              autoComplete="new-password"
              placeholder="••••••••"
              required
              error={errors.confirmPassword?.message}
              {...register('confirmPassword')}
            />
          </div>

          <div className="bg-gray-800/50 border border-purple-500/30 rounded-2xl p-5 backdrop-blur-sm">
            <p className="text-xs text-gray-300">
              <strong className="text-purple-400">Password requirements:</strong>
              <ul className="list-disc list-inside mt-2 space-y-1">
                <li>At least 8 characters long</li>
                <li>One uppercase letter</li>
                <li>One lowercase letter</li>
                <li>One number</li>
              </ul>
            </p>
          </div>

          <Button
            type="submit"
            variant="primary"
            isLoading={isSubmitting}
            className="w-full"
          >
            {isSubmitting ? 'Resetting Password...' : 'Reset Password'}
          </Button>
        </form>

        <div className="text-center">
          <p className="text-sm text-gray-400">
            Remember your password?{' '}
            <Link to="/login" className="font-semibold text-purple-400 hover:text-purple-300 transition-colors">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
