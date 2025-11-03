import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { CheckCircle, XCircle, Trophy, Eye, RotateCcw, ArrowLeft } from 'lucide-react';
import Button from '../../components/ui/Button';
import { getStudentQuizAttempts, getBestQuizAttempt } from '../../services/quizService';
import type { QuizAttempt } from '../../types/api';

export default function QuizAttemptsPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [attempts, setAttempts] = useState<QuizAttempt[]>([]);
  const [bestAttempt, setBestAttempt] = useState<QuizAttempt | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchAttempts();
  }, [id]);

  const fetchAttempts = async () => {
    if (!id) return;

    try {
      setIsLoading(true);
      const [attemptsResponse, bestResponse] = await Promise.all([
        getStudentQuizAttempts(parseInt(id)),
        getBestQuizAttempt(parseInt(id)).catch(() => null),
      ]);

      setAttempts(attemptsResponse);
      if (bestResponse) {
        setBestAttempt(bestResponse);
      }
    } catch (err: any) {
      console.error('Error fetching attempts:', err);
      toast.error('Failed to load quiz attempts');
    } finally {
      setIsLoading(false);
    }
  };

  const handleViewDetails = (attemptId: number) => {
    navigate(`/quiz/${id}/result/${attemptId}`);
  };

  const handleRetake = () => {
    navigate(`/quiz/${id}/take`);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
          <p className="text-gray-400 mt-4">Loading attempts...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-100 mb-2">Quiz Attempts</h1>
          <p className="text-gray-400">View your attempt history and scores</p>
        </div>

        {/* Best Score Card */}
        {bestAttempt && (
          <div className="bg-gradient-to-br from-yellow-900/40 to-yellow-800/20 border border-yellow-700 rounded-xl p-6 mb-8">
            <div className="flex items-start gap-4">
              <Trophy className="w-12 h-12 text-yellow-400 flex-shrink-0" />
              <div className="flex-1">
                <h2 className="text-2xl font-bold text-gray-100 mb-2">Best Score</h2>
                <div className="flex items-center gap-6">
                  <div>
                    <div className="text-4xl font-bold text-yellow-400">
                      {bestAttempt.scorePercentage.toFixed(0)}%
                    </div>
                    <div className="text-gray-300 text-sm">
                      {bestAttempt.score} / {bestAttempt.maxScore} points
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    {bestAttempt.passed ? (
                      <>
                        <CheckCircle className="w-6 h-6 text-green-400" />
                        <span className="text-green-400 font-semibold">PASSED</span>
                      </>
                    ) : (
                      <>
                        <XCircle className="w-6 h-6 text-red-400" />
                        <span className="text-red-400 font-semibold">FAILED</span>
                      </>
                    )}
                  </div>
                </div>
                <p className="text-gray-400 text-sm mt-2">
                  Achieved on {new Date(bestAttempt.attemptedAt).toLocaleDateString()}
                </p>
              </div>
              <Button
                variant="secondary"
                onClick={() => handleViewDetails(bestAttempt.id)}
              >
                <Eye className="w-4 h-4 mr-2" />
                View
              </Button>
            </div>
          </div>
        )}

        {/* Attempts List */}
        <div className="space-y-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-bold text-gray-100">
              All Attempts ({attempts.length})
            </h2>
            <Button variant="primary" onClick={handleRetake}>
              <RotateCcw className="w-4 h-4 mr-2" />
              Retake Quiz
            </Button>
          </div>

          {attempts.length === 0 ? (
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center">
              <p className="text-gray-400 text-lg mb-4">No attempts yet</p>
              <p className="text-gray-500 mb-6">
                Take the quiz to see your results here
              </p>
              <Button variant="primary" onClick={handleRetake}>
                Take Quiz Now
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {attempts.map((attempt, index) => (
                <div
                  key={attempt.id}
                  className={`bg-gray-800 border rounded-xl p-6 hover:border-gray-600 transition-colors ${
                    bestAttempt?.id === attempt.id
                      ? 'border-yellow-700 bg-yellow-900/10'
                      : 'border-gray-700'
                  }`}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-3">
                        <h3 className="text-xl font-semibold text-gray-100">
                          Attempt #{index + 1}
                        </h3>
                        {bestAttempt?.id === attempt.id && (
                          <span className="inline-flex items-center gap-1 px-2 py-1 bg-yellow-900/30 text-yellow-400 text-xs font-medium rounded-full">
                            <Trophy className="w-3 h-3" />
                            Best
                          </span>
                        )}
                      </div>

                      <div className="grid grid-cols-3 gap-6 mb-3">
                        <div>
                          <div className="text-gray-400 text-sm mb-1">Score</div>
                          <div className="text-2xl font-bold text-gray-100">
                            {attempt.scorePercentage.toFixed(0)}%
                          </div>
                          <div className="text-gray-400 text-sm">
                            {attempt.score} / {attempt.maxScore} points
                          </div>
                        </div>

                        <div>
                          <div className="text-gray-400 text-sm mb-1">Status</div>
                          <div className="flex items-center gap-2 mt-2">
                            {attempt.passed ? (
                              <>
                                <CheckCircle className="w-5 h-5 text-green-400" />
                                <span className="text-green-400 font-semibold">PASSED</span>
                              </>
                            ) : (
                              <>
                                <XCircle className="w-5 h-5 text-red-400" />
                                <span className="text-red-400 font-semibold">FAILED</span>
                              </>
                            )}
                          </div>
                        </div>

                        <div>
                          <div className="text-gray-400 text-sm mb-1">Date</div>
                          <div className="text-gray-300 mt-2">
                            {new Date(attempt.attemptedAt).toLocaleDateString()}
                          </div>
                          <div className="text-gray-500 text-sm">
                            {new Date(attempt.attemptedAt).toLocaleTimeString()}
                          </div>
                        </div>
                      </div>

                      {/* Progress Bar */}
                      <div className="w-full bg-gray-700 rounded-full h-2">
                        <div
                          className={`h-2 rounded-full ${
                            attempt.passed ? 'bg-green-500' : 'bg-red-500'
                          }`}
                          style={{ width: `${attempt.scorePercentage}%` }}
                        />
                      </div>
                    </div>

                    <Button
                      variant="secondary"
                      onClick={() => handleViewDetails(attempt.id)}
                      className="ml-6"
                    >
                      <Eye className="w-4 h-4 mr-2" />
                      View Details
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Back Button */}
        <div className="mt-8 flex justify-center">
          <Button variant="secondary" onClick={() => navigate(-1)}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Course
          </Button>
        </div>
      </div>
    </div>
  );
}
