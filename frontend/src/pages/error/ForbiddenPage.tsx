import { Link } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';
import Button from '../../components/ui/Button';

export default function ForbiddenPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4">
      <div className="max-w-md w-full text-center space-y-6">
        <div className="flex justify-center">
          <div className="bg-gradient-to-br from-red-600 to-orange-600 p-5 rounded-3xl shadow-2xl">
            <ShieldAlert className="h-16 w-16 text-white" />
          </div>
        </div>

        <h1 className="text-6xl font-bold bg-gradient-to-r from-red-400 to-orange-400 bg-clip-text text-transparent">
          403
        </h1>

        <h2 className="text-3xl font-bold text-gray-200">
          Access Denied
        </h2>

        <div className="bg-gray-800/50 border border-red-500/30 rounded-2xl p-5 backdrop-blur-sm">
          <p className="text-sm text-gray-300">
            You don't have permission to access this page.
            This area is restricted to specific user roles.
          </p>
        </div>

        <div className="space-y-3">
          <Link to="/dashboard">
            <Button variant="primary" className="w-full">
              Go to Dashboard
            </Button>
          </Link>
          <Link to="/">
            <Button variant="secondary" className="w-full">
              Go to Home
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
