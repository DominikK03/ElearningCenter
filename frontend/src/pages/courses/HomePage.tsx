import { Link } from 'react-router-dom';
import { BookOpen, ArrowRight, LogIn, UserPlus } from 'lucide-react';
import Button from '../../components/ui/Button';

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gray-950">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
        <div className="flex justify-end gap-3 mb-8">
          <Link to="/login">
            <Button variant="ghost" className="px-6 py-2">
              <LogIn className="w-4 h-4" />
              Login
            </Button>
          </Link>
          <Link to="/register">
            <Button variant="primary" className="px-6 py-2">
              <UserPlus className="w-4 h-4" />
              Register
            </Button>
          </Link>
        </div>

        <div className="text-center mb-16">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-purple-600 to-pink-600 rounded-full mb-6">
            <BookOpen className="w-10 h-10 text-white" />
          </div>
          <h1 className="text-5xl md:text-6xl font-bold text-gray-100 mb-6 bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
            E-Learning Center
          </h1>
          <p className="text-xl text-gray-400 max-w-2xl mx-auto mb-8">
            Discover and enroll in courses taught by expert instructors.
            Expand your knowledge and advance your career.
          </p>
          <div className="flex justify-center">
            <Link to="/courses">
              <Button variant="primary" className="text-lg px-8 py-4">
                Browse Courses
                <ArrowRight className="w-5 h-5" />
              </Button>
            </Link>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-20">
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-purple-600 transition-all duration-200">
            <div className="w-12 h-12 bg-purple-900/30 rounded-lg flex items-center justify-center mb-4">
              <BookOpen className="w-6 h-6 text-purple-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-100 mb-2">
              Quality Content
            </h3>
            <p className="text-gray-400">
              Learn from carefully curated courses with structured lessons and materials.
            </p>
          </div>

          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-purple-600 transition-all duration-200">
            <div className="w-12 h-12 bg-purple-900/30 rounded-lg flex items-center justify-center mb-4">
              <BookOpen className="w-6 h-6 text-purple-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-100 mb-2">
              Track Progress
            </h3>
            <p className="text-gray-400">
              Monitor your learning journey with detailed progress tracking and quizzes.
            </p>
          </div>

          <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-purple-600 transition-all duration-200">
            <div className="w-12 h-12 bg-purple-900/30 rounded-lg flex items-center justify-center mb-4">
              <BookOpen className="w-6 h-6 text-purple-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-100 mb-2">
              Expert Instructors
            </h3>
            <p className="text-gray-400">
              Learn from experienced instructors passionate about teaching.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}