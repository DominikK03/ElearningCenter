import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { Mail, CheckCircle } from 'lucide-react';

import { userService } from '../../services';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';

// Validation schema
const resendSchema = z.object({
  email: z.string()
    .min(1, 'Email is required')
    .email('Invalid email address'),
});

type ResendFormData = z.infer<typeof resendSchema>;

export default function ResendVerificationPage() {
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ResendFormData>({
    resolver: zodResolver(resendSchema),
  });

  const onSubmit = async (data: ResendFormData) => {
    try {
      await userService.resendVerification(data);
      setSuccess(true);
      toast.success('Verification email sent! Check your inbox.');
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to resend verification email';
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
            Verification Email Sent!
          </h2>
          <div className="bg-gray-800/50 border border-purple-500/30 rounded-2xl p-5 backdrop-blur-sm">
            <p className="text-sm text-gray-300">
              We've sent a new verification link to your email address.
              Please check your inbox (and spam folder) and click the link to verify your account.
            </p>
          </div>
          <div className="space-y-3">
            <p className="text-gray-400 text-sm">
              Didn't receive the email? Wait a few minutes and check your spam folder.
            </p>
            <div className="flex flex-col gap-3">
              <Button
                onClick={() => setSuccess(false)}
                variant="secondary"
                className="w-full"
              >
                Resend Again
              </Button>
              <Link to="/login">
                <Button variant="ghost" className="w-full">
                  Back to Login
                </Button>
              </Link>
            </div>
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
              <Mail className="h-10 w-10 text-white" />
            </div>
          </div>
          <h2 className="mt-8 text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
            Resend Verification Email
          </h2>
          <p className="mt-3 text-sm text-gray-400">
            Enter your email address and we'll send you a new verification link.
          </p>
        </div>

        <div className="bg-gray-800/50 border border-yellow-500/30 rounded-2xl p-5 backdrop-blur-sm">
          <p className="text-sm text-gray-300">
            If you didn't receive the verification email, it might be in your spam folder.
            Make sure to check there before requesting a new one.
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          <Input
            label="Email Address"
            type="email"
            autoComplete="email"
            placeholder="john@example.com"
            required
            error={errors.email?.message}
            {...register('email')}
          />

          <Button
            type="submit"
            variant="primary"
            isLoading={isSubmitting}
            className="w-full"
          >
            {isSubmitting ? 'Sending...' : 'Send Verification Email'}
          </Button>
        </form>

        <div className="text-center space-y-2">
          <p className="text-sm text-gray-400">
            Already verified?{' '}
            <Link to="/login" className="font-semibold text-purple-400 hover:text-purple-300 transition-colors">
              Sign in
            </Link>
          </p>
          <p className="text-sm text-gray-400">
            Don't have an account?{' '}
            <Link to="/register" className="font-semibold text-purple-400 hover:text-purple-300 transition-colors">
              Register
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
