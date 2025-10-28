import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { CheckCircle, XCircle, Loader2 } from 'lucide-react';
import { userService } from '../../services';
import Button from '../../components/ui/Button';

type VerificationState = 'loading' | 'success' | 'error';

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [state, setState] = useState<VerificationState>('loading');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [countdown, setCountdown] = useState(5);

  useEffect(() => {
    const token = searchParams.get('token');

    if (!token) {
      setState('error');
      setErrorMessage('Verification token is missing from the URL.');
      return;
    }

    const verifyToken = async () => {
      try {
        await userService.verifyEmail({ token });
        setState('success');
      } catch (err: any) {
        setState('error');
        const message = err.response?.data?.message || 'Email verification failed. The token may be invalid or expired.';
        setErrorMessage(message);
      }
    };

    verifyToken();
  }, [searchParams]);

  useEffect(() => {
    if (state === 'success' && countdown > 0) {
      const timer = setTimeout(() => {
        setCountdown(countdown - 1);
      }, 1000);
      return () => clearTimeout(timer);
    } else if (state === 'success' && countdown === 0) {
      navigate('/login');
    }
  }, [state, countdown, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4">
      <div className="max-w-md w-full">
        {state === 'loading' && (
          <div className="text-center space-y-6">
            <div className="flex justify-center">
              <div className="bg-gradient-to-br from-purple-600 to-pink-600 p-5 rounded-3xl shadow-2xl">
                <Loader2 className="h-16 w-16 text-white animate-spin" />
              </div>
            </div>
            <h2 className="text-3xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
              Verifying your email...
            </h2>
            <p className="text-gray-400">
              Please wait while we verify your email address.
            </p>
          </div>
        )}

        {state === 'success' && (
          <div className="text-center space-y-6">
            <div className="flex justify-center">
              <div className="bg-gradient-to-br from-green-600 to-emerald-600 p-5 rounded-3xl shadow-2xl">
                <CheckCircle className="h-16 w-16 text-white" />
              </div>
            </div>
            <h2 className="text-3xl font-bold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent">
              Email Verified Successfully!
            </h2>
            <div className="bg-gray-800/50 border border-green-500/30 rounded-2xl p-5 backdrop-blur-sm">
              <p className="text-sm text-gray-300">
                Your email has been verified. You can now log in to your account.
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
        )}

        {state === 'error' && (
            <div className="text-center space-y-6">
            <div className="flex justify-center">
              <div className="bg-gradient-to-br from-red-600 to-orange-600 p-5 rounded-3xl shadow-2xl">
                <XCircle className="h-16 w-16 text-white" />
              </div>
            </div>
            <h2 className="text-3xl font-bold bg-gradient-to-r from-red-400 to-orange-400 bg-clip-text text-transparent">
              Verification Failed
            </h2>
            <div className="bg-gray-800/50 border border-red-500/30 rounded-2xl p-5 backdrop-blur-sm">
              <p className="text-sm text-gray-300">{errorMessage}</p>
            </div>
            <div className="space-y-3">
              <p className="text-gray-400">
                The verification link may have expired or is invalid.
              </p>
              <div className="flex flex-col gap-3">
                <Link to="/resend-verification">
                  <Button variant="primary" className="w-full">
                    Resend Verification Email
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
        )}
      </div>
    </div>
  );
}
