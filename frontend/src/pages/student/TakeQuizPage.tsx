import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import Button from '../../components/ui/Button';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { getQuizForStudent, submitQuizAttempt } from '../../services/quizService';
import type { Quiz, StudentAnswer } from '../../types/api';

export default function TakeQuizPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState<StudentAnswer[]>([]);
  const [showSubmitDialog, setShowSubmitDialog] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchQuiz();
  }, [id]);

  const fetchQuiz = async () => {
    if (!id) return;

    try {
      setIsLoading(true);
      const quizData = await getQuizForStudent(parseInt(id));
      setQuiz(quizData);

      // Initialize empty answers for all questions
      const initialAnswers: StudentAnswer[] = quizData.questions.map((q) => ({
        questionId: q.id,
        selectedAnswerIndexes: [],
      }));
      setAnswers(initialAnswers);
    } catch (err: any) {
      console.error('Error fetching quiz:', err);
      toast.error('Failed to load quiz');
      navigate(-1);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAnswerChange = (answerIndex: number, checked: boolean) => {
    if (!quiz) return;

    const currentQuestion = quiz.questions[currentQuestionIndex];
    const newAnswers = [...answers];
    const answerObj = newAnswers.find((a) => a.questionId === currentQuestion.id);

    if (!answerObj) return;

    if (currentQuestion.type === 'SINGLE_CHOICE' || currentQuestion.type === 'TRUE_FALSE') {
      answerObj.selectedAnswerIndexes = checked ? [answerIndex] : [];
    } else {
      if (checked) {
        if (!answerObj.selectedAnswerIndexes.includes(answerIndex)) {
          answerObj.selectedAnswerIndexes.push(answerIndex);
        }
      } else {
        answerObj.selectedAnswerIndexes = answerObj.selectedAnswerIndexes.filter(
          (idx) => idx !== answerIndex
        );
      }
    }

    setAnswers(newAnswers);
  };

  const isAnswerSelected = (answerIndex: number): boolean => {
    if (!quiz) return false;
    const currentQuestion = quiz.questions[currentQuestionIndex];
    const answerObj = answers.find((a) => a.questionId === currentQuestion.id);
    return answerObj?.selectedAnswerIndexes.includes(answerIndex) || false;
  };

  const goToNextQuestion = () => {
    if (!quiz) return;
    if (currentQuestionIndex < quiz.questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  const goToPreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
  };

  const getAnsweredCount = (): number => {
    return answers.filter((a) => a.selectedAnswerIndexes.length > 0).length;
  };

  const handleSubmit = async () => {
    if (!id) return;

    try {
      setIsSubmitting(true);

      const attempt = await submitQuizAttempt(parseInt(id), { answers });

      toast.success('Quiz submitted successfully!');

      navigate(`/quiz/${id}/result/${attempt.id}`);
    } catch (err: any) {
      console.error('Error submitting quiz:', err);
      toast.error(err.response?.data?.message || 'Failed to submit quiz');
    } finally {
      setIsSubmitting(false);
      setShowSubmitDialog(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
          <p className="text-gray-400 mt-4">Loading quiz...</p>
        </div>
      </div>
    );
  }

  if (!quiz || quiz.questions.length === 0) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-400 text-lg">Quiz not found or has no questions</p>
          <Button variant="secondary" onClick={() => navigate(-1)} className="mt-4">
            Go Back
          </Button>
        </div>
      </div>
    );
  }

  const currentQuestion = quiz.questions[currentQuestionIndex];
  const answeredCount = getAnsweredCount();
  const totalQuestions = quiz.questions.length;

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-100 mb-2">{quiz.title}</h1>
          <p className="text-gray-400">Passing Score: {quiz.passingScore}%</p>
        </div>

        {/* Progress */}
        <div className="bg-gray-800 border border-gray-700 rounded-xl p-4 mb-6">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-300 font-medium">
              Question {currentQuestionIndex + 1} of {totalQuestions}
            </span>
            <span className="text-gray-400 text-sm">
              Answered: {answeredCount} / {totalQuestions}
            </span>
          </div>
          <div className="w-full bg-gray-700 rounded-full h-2">
            <div
              className="bg-purple-500 h-2 rounded-full transition-all duration-300"
              style={{ width: `${((currentQuestionIndex + 1) / totalQuestions) * 100}%` }}
            />
          </div>
        </div>

        {/* Question Card */}
        <div className="bg-gray-800 border border-gray-700 rounded-xl p-8 mb-6">
          {/* Question Type Badge */}
          <div className="mb-4">
            <span className="inline-block px-3 py-1 bg-purple-900/30 text-purple-400 text-sm font-medium rounded-full">
              {currentQuestion.type === 'SINGLE_CHOICE' && 'Single Choice'}
              {currentQuestion.type === 'MULTIPLE_CHOICE' && 'Multiple Choice'}
              {currentQuestion.type === 'TRUE_FALSE' && 'True/False'}
            </span>
            <span className="ml-3 text-gray-400 text-sm">
              {currentQuestion.points} {currentQuestion.points === 1 ? 'point' : 'points'}
            </span>
          </div>

          {/* Question Text */}
          <h2 className="text-2xl font-semibold text-gray-100 mb-6">
            {currentQuestion.text}
          </h2>

          {/* Answers */}
          <div className="space-y-3">
            {currentQuestion.answers.map((answer, index) => {
              const isSelected = isAnswerSelected(index);
              const inputType = currentQuestion.type === 'SINGLE_CHOICE' || currentQuestion.type === 'TRUE_FALSE'
                ? 'radio'
                : 'checkbox';

              return (
                <label
                  key={index}
                  className={`flex items-center gap-4 p-4 rounded-lg border-2 cursor-pointer transition-all ${
                    isSelected
                      ? 'border-purple-500 bg-purple-900/20'
                      : 'border-gray-700 bg-gray-900 hover:border-gray-600'
                  }`}
                >
                  <input
                    type={inputType}
                    name={`question-${currentQuestion.id}`}
                    checked={isSelected}
                    onChange={(e) => handleAnswerChange(index, e.target.checked)}
                    className="w-5 h-5 text-purple-600 focus:ring-purple-500"
                  />
                  <span className="text-gray-200 text-lg flex-1">{answer.text}</span>
                </label>
              );
            })}
          </div>

          {/* Helper Text */}
          <p className="text-sm text-gray-400 mt-4">
            {currentQuestion.type === 'SINGLE_CHOICE' && 'Select one answer'}
            {currentQuestion.type === 'MULTIPLE_CHOICE' && 'Select all that apply'}
            {currentQuestion.type === 'TRUE_FALSE' && 'Select one answer'}
          </p>
        </div>

        {/* Navigation */}
        <div className="flex items-center justify-between">
          <Button
            variant="secondary"
            onClick={goToPreviousQuestion}
            disabled={currentQuestionIndex === 0}
          >
            <ChevronLeft className="w-4 h-4 mr-2" />
            Previous
          </Button>

          {currentQuestionIndex < totalQuestions - 1 ? (
            <Button variant="primary" onClick={goToNextQuestion}>
              Next
              <ChevronRight className="w-4 h-4 ml-2" />
            </Button>
          ) : (
            <Button
              variant="primary"
              onClick={() => setShowSubmitDialog(true)}
              className="bg-green-600 hover:bg-green-700"
            >
              Submit Quiz
            </Button>
          )}
        </div>

        {/* Question Navigation Dots */}
        <div className="mt-8 flex items-center justify-center gap-2 flex-wrap">
          {quiz.questions.map((q, index) => {
            const isAnswered = answers.find((a) => a.questionId === q.id)?.selectedAnswerIndexes.length! > 0;
            const isCurrent = index === currentQuestionIndex;

            return (
              <button
                key={q.id}
                onClick={() => setCurrentQuestionIndex(index)}
                className={`w-8 h-8 rounded-full font-medium text-sm transition-all ${
                  isCurrent
                    ? 'bg-purple-600 text-white ring-2 ring-purple-400'
                    : isAnswered
                    ? 'bg-green-900/40 text-green-400 border border-green-700'
                    : 'bg-gray-800 text-gray-400 border border-gray-700 hover:border-gray-600'
                }`}
                title={`Question ${index + 1}${isAnswered ? ' (answered)' : ''}`}
              >
                {index + 1}
              </button>
            );
          })}
        </div>
      </div>

      {/* Submit Confirmation Dialog */}
      <ConfirmDialog
        isOpen={showSubmitDialog}
        onClose={() => setShowSubmitDialog(false)}
        onConfirm={handleSubmit}
        title="Submit Quiz"
        message={`Are you sure you want to submit? You have answered ${answeredCount} out of ${totalQuestions} questions. You cannot change your answers after submission.`}
        confirmText="Submit Quiz"
        cancelText="Review Answers"
        variant="warning"
        isLoading={isSubmitting}
      />
    </div>
  );
}
