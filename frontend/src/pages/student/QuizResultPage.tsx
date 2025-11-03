import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { CheckCircle, XCircle, RotateCcw, ArrowLeft, List } from 'lucide-react';
import Button from '../../components/ui/Button';
import { getQuizAttemptDetails } from '../../services/quizService';
import type { QuizAttempt } from '../../types/api';

export default function QuizResultPage() {
  const { quizId, attemptId } = useParams<{ quizId: string; attemptId: string }>();
  const navigate = useNavigate();

  const [attempt, setAttempt] = useState<QuizAttempt | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchAttempt();
  }, [quizId, attemptId]);

  const fetchAttempt = async () => {
    if (!quizId || !attemptId) return;

    try {
      setIsLoading(true);
      const attemptData = await getQuizAttemptDetails(parseInt(quizId), parseInt(attemptId));
      setAttempt(attemptData);
    } catch (err: any) {
      console.error('Error fetching attempt:', err);
      toast.error('Failed to load quiz results');
      navigate(-1);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRetake = () => {
    if (!quizId) return;
    navigate(`/quiz/${quizId}/take`);
  };

  const handleViewAttempts = () => {
    if (!quizId) return;
    navigate(`/quiz/${quizId}/attempts`);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
          <p className="text-gray-400 mt-4">Loading results...</p>
        </div>
      </div>
    );
  }

  if (!attempt) return null;

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Results Card */}
        <div className="bg-gray-800 border border-gray-700 rounded-xl overflow-hidden">
          {/* Header */}
          <div className={`p-8 text-center ${
            attempt.passed
              ? 'bg-gradient-to-br from-green-900/40 to-green-800/20 border-b border-green-700'
              : 'bg-gradient-to-br from-red-900/40 to-red-800/20 border-b border-red-700'
          }`}>
            {attempt.passed ? (
              <CheckCircle className="w-20 h-20 text-green-400 mx-auto mb-4" />
            ) : (
              <XCircle className="w-20 h-20 text-red-400 mx-auto mb-4" />
            )}

            <h1 className="text-4xl font-bold text-gray-100 mb-2">
              {attempt.passed ? 'Congratulations!' : 'Not Passed'}
            </h1>
            <p className="text-xl text-gray-300 mb-6">
              {attempt.passed
                ? 'You have successfully passed this quiz!'
                : 'Unfortunately, you did not pass this quiz.'}
            </p>

            {/* Score */}
            <div className="flex items-center justify-center gap-8 mb-4">
              <div className="text-center">
                <div className="text-5xl font-bold text-gray-100 mb-1">
                  {attempt.scorePercentage.toFixed(0)}%
                </div>
                <div className="text-gray-400">Your Score</div>
              </div>
              <div className="text-4xl text-gray-600">|</div>
              <div className="text-center">
                <div className="text-5xl font-bold text-gray-300 mb-1">
                  {attempt.score} / {attempt.maxScore}
                </div>
                <div className="text-gray-400">Points</div>
              </div>
            </div>

            <p className="text-gray-400 mt-4">
              Attempted: {new Date(attempt.attemptedAt).toLocaleString()}
            </p>
          </div>

          {/* Stats */}
          <div className="p-8">
            <h2 className="text-2xl font-bold text-gray-100 mb-6">Quiz Statistics</h2>
            <div className="grid grid-cols-2 gap-6">
              <div className="bg-gray-900 rounded-lg p-6 border border-gray-700">
                <div className="text-gray-400 text-sm mb-2">Score Percentage</div>
                <div className="text-3xl font-bold text-gray-100">
                  {attempt.scorePercentage.toFixed(1)}%
                </div>
                <div className="w-full bg-gray-700 rounded-full h-2 mt-3">
                  <div
                    className={`h-2 rounded-full ${
                      attempt.passed ? 'bg-green-500' : 'bg-red-500'
                    }`}
                    style={{ width: `${attempt.scorePercentage}%` }}
                  />
                </div>
              </div>

              <div className="bg-gray-900 rounded-lg p-6 border border-gray-700">
                <div className="text-gray-400 text-sm mb-2">Status</div>
                <div className="flex items-center gap-2 mt-2">
                  {attempt.passed ? (
                    <>
                      <CheckCircle className="w-6 h-6 text-green-400" />
                      <span className="text-2xl font-bold text-green-400">PASSED</span>
                    </>
                  ) : (
                    <>
                      <XCircle className="w-6 h-6 text-red-400" />
                      <span className="text-2xl font-bold text-red-400">FAILED</span>
                    </>
                  )}
                </div>
              </div>
            </div>

            {!attempt.passed && (
              <div className="mt-6 bg-yellow-900/20 border border-yellow-700/50 rounded-lg p-4">
                <div className="flex items-start gap-3">
                  <div className="flex-shrink-0">
                    <svg
                      className="w-5 h-5 text-yellow-500"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fillRule="evenodd"
                        d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                        clipRule="evenodd"
                      />
                    </svg>
                  </div>
                  <div>
                    <h3 className="text-yellow-500 font-semibold text-sm">Keep Trying!</h3>
                    <p className="text-gray-300 text-sm mt-1">
                      Don't give up! Review the material and try again. You can retake this quiz to improve your score.
                    </p>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Actions */}
          <div className="p-8 bg-gray-900/50 border-t border-gray-700">
            <div className="flex flex-wrap gap-3 justify-center">
              <Button
                variant="secondary"
                onClick={() => navigate(-1)}
              >
                <ArrowLeft className="w-4 h-4 mr-2" />
                Back to Course
              </Button>
              <Button
                variant="secondary"
                onClick={handleViewAttempts}
              >
                <List className="w-4 h-4 mr-2" />
                View All Attempts
              </Button>
              <Button
                variant="primary"
                onClick={handleRetake}
              >
                <RotateCcw className="w-4 h-4 mr-2" />
                Retake Quiz
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
