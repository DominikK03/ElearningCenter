import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Trash2, Edit, Plus, Check, X } from 'lucide-react';
import Button from '../../components/ui/Button';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import {
  getQuizDetails,
  updateQuiz,
  deleteQuiz,
  addQuestion,
  updateQuestion,
  deleteQuestion,
} from '../../services/quizService';
import type {
  Quiz,
  Question,
  QuestionType,
  AnswerRequest,
  AddQuestionRequest,
  UpdateQuestionRequest,
} from '../../types/api';

interface QuestionFormData {
  text: string;
  type: QuestionType;
  points: number;
  answers: AnswerRequest[];
}

export default function ManageQuizPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Quiz edit state
  const [isEditingQuiz, setIsEditingQuiz] = useState(false);
  const [quizTitle, setQuizTitle] = useState('');
  const [quizPassingScore, setQuizPassingScore] = useState(70);
  const [isUpdatingQuiz, setIsUpdatingQuiz] = useState(false);

  // Delete quiz state
  const [showDeleteQuizDialog, setShowDeleteQuizDialog] = useState(false);
  const [isDeletingQuiz, setIsDeletingQuiz] = useState(false);

  // Question modal state
  const [showQuestionModal, setShowQuestionModal] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState<Question | null>(null);
  const [isSavingQuestion, setIsSavingQuestion] = useState(false);

  // Delete question state
  const [showDeleteQuestionDialog, setShowDeleteQuestionDialog] = useState(false);
  const [questionToDelete, setQuestionToDelete] = useState<Question | null>(null);
  const [isDeletingQuestion, setIsDeletingQuestion] = useState(false);

  // Question form state
  const [questionForm, setQuestionForm] = useState<QuestionFormData>({
    text: '',
    type: 'SINGLE_CHOICE',
    points: 1,
    answers: [
      { text: '', correct: false },
      { text: '', correct: false },
    ],
  });

  useEffect(() => {
    fetchQuiz();
  }, [id]);

  const fetchQuiz = async () => {
    if (!id) return;

    try {
      setIsLoading(true);
      const quiz = await getQuizDetails(parseInt(id));
      setQuiz(quiz);
      setQuizTitle(quiz.title);
      setQuizPassingScore(quiz.passingScore);
    } catch (err: any) {
      console.error('Error fetching quiz:', err);
      toast.error('Failed to load quiz');
      navigate('/my-teaching');
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateQuiz = async () => {
    if (!id || !quizTitle.trim()) return;

    try {
      setIsUpdatingQuiz(true);
      await updateQuiz(parseInt(id), {
        title: quizTitle.trim(),
        passingScore: quizPassingScore,
      });
      toast.success('Quiz updated successfully!');
      setIsEditingQuiz(false);
      await fetchQuiz();
    } catch (err: any) {
      console.error('Error updating quiz:', err);
      toast.error(err.response?.data?.message || 'Failed to update quiz');
    } finally {
      setIsUpdatingQuiz(false);
    }
  };

  const handleDeleteQuiz = async () => {
    if (!id) return;

    try {
      setIsDeletingQuiz(true);
      await deleteQuiz(parseInt(id));
      toast.success('Quiz deleted successfully!');
      navigate('/my-teaching');
    } catch (err: any) {
      console.error('Error deleting quiz:', err);
      toast.error(err.response?.data?.message || 'Failed to delete quiz');
      setIsDeletingQuiz(false);
    }
  };

  const openAddQuestionModal = () => {
    setEditingQuestion(null);
    setQuestionForm({
      text: '',
      type: 'SINGLE_CHOICE',
      points: 1,
      answers: [
        { text: '', correct: false },
        { text: '', correct: false },
      ],
    });
    setShowQuestionModal(true);
  };

  const openEditQuestionModal = (question: Question) => {
    setEditingQuestion(question);
    setQuestionForm({
      text: question.text,
      type: question.type,
      points: question.points,
      answers: question.answers.map((a) => ({ text: a.text, correct: a.correct || false })),
    });
    setShowQuestionModal(true);
  };

  const handleSaveQuestion = async () => {
    if (!id) return;

    // Validation
    if (!questionForm.text.trim()) {
      toast.error('Please enter question text');
      return;
    }

    if (questionForm.answers.length < 2) {
      toast.error('Please add at least 2 answers');
      return;
    }

    if (questionForm.answers.some((a) => !a.text.trim())) {
      toast.error('All answers must have text');
      return;
    }

    const correctCount = questionForm.answers.filter((a) => a.correct).length;

    if (questionForm.type === 'SINGLE_CHOICE' && correctCount !== 1) {
      toast.error('Single choice questions must have exactly 1 correct answer');
      return;
    }

    if (questionForm.type === 'MULTIPLE_CHOICE' && correctCount < 1) {
      toast.error('Multiple choice questions must have at least 1 correct answer');
      return;
    }

    if (questionForm.type === 'TRUE_FALSE') {
      if (questionForm.answers.length !== 2) {
        toast.error('True/False questions must have exactly 2 answers');
        return;
      }
      if (correctCount !== 1) {
        toast.error('True/False questions must have exactly 1 correct answer');
        return;
      }
    }

    try {
      setIsSavingQuestion(true);

      if (editingQuestion) {
        // Update existing question
        const data: UpdateQuestionRequest = {
          text: questionForm.text.trim(),
          points: questionForm.points,
          orderIndex: editingQuestion.orderIndex,
          answers: questionForm.answers.map((a) => ({ text: a.text.trim(), correct: a.correct })),
        };
        await updateQuestion(parseInt(id), editingQuestion.id, data);
        toast.success('Question updated successfully!');
      } else {
        // Add new question
        const data: AddQuestionRequest = {
          text: questionForm.text.trim(),
          type: questionForm.type,
          points: questionForm.points,
          orderIndex: quiz?.questions.length || 0,
          answers: questionForm.answers.map((a) => ({ text: a.text.trim(), correct: a.correct })),
        };
        await addQuestion(parseInt(id), data);
        toast.success('Question added successfully!');
      }

      setShowQuestionModal(false);
      await fetchQuiz();
    } catch (err: any) {
      console.error('Error saving question:', err);
      toast.error(err.response?.data?.message || 'Failed to save question');
    } finally {
      setIsSavingQuestion(false);
    }
  };

  const handleDeleteQuestion = async () => {
    if (!id || !questionToDelete) return;

    try {
      setIsDeletingQuestion(true);
      await deleteQuestion(parseInt(id), questionToDelete.id);
      toast.success('Question deleted successfully!');
      setShowDeleteQuestionDialog(false);
      setQuestionToDelete(null);
      await fetchQuiz();
    } catch (err: any) {
      console.error('Error deleting question:', err);
      toast.error(err.response?.data?.message || 'Failed to delete question');
    } finally {
      setIsDeletingQuestion(false);
    }
  };

  const addAnswer = () => {
    setQuestionForm({
      ...questionForm,
      answers: [...questionForm.answers, { text: '', correct: false }],
    });
  };

  const removeAnswer = (index: number) => {
    if (questionForm.answers.length <= 2) {
      toast.error('Questions must have at least 2 answers');
      return;
    }
    setQuestionForm({
      ...questionForm,
      answers: questionForm.answers.filter((_, i) => i !== index),
    });
  };

  const updateAnswer = (index: number, field: 'text' | 'correct', value: string | boolean) => {
    const newAnswers = [...questionForm.answers];
    newAnswers[index] = { ...newAnswers[index], [field]: value };

    // For SINGLE_CHOICE, uncheck other answers when one is checked
    if (field === 'correct' && value === true && questionForm.type === 'SINGLE_CHOICE') {
      newAnswers.forEach((a, i) => {
        if (i !== index) a.correct = false;
      });
    }

    setQuestionForm({ ...questionForm, answers: newAnswers });
  };

  const getQuestionTypeLabel = (type: QuestionType): string => {
    switch (type) {
      case 'SINGLE_CHOICE':
        return 'Single Choice';
      case 'MULTIPLE_CHOICE':
        return 'Multiple Choice';
      case 'TRUE_FALSE':
        return 'True/False';
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

  if (!quiz) return null;

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-start justify-between mb-4">
            <div className="flex-1">
              {isEditingQuiz ? (
                <div className="space-y-3">
                  <input
                    type="text"
                    value={quizTitle}
                    onChange={(e) => setQuizTitle(e.target.value)}
                    className="w-full px-4 py-2 text-3xl font-bold bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  />
                  <div className="flex items-center gap-4">
                    <label className="text-gray-300">Passing Score:</label>
                    <input
                      type="number"
                      value={quizPassingScore}
                      onChange={(e) => setQuizPassingScore(parseInt(e.target.value))}
                      min={0}
                      max={100}
                      className="px-3 py-1 bg-gray-900 border border-gray-700 rounded text-gray-100 w-20"
                    />
                    <span className="text-gray-400">%</span>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      variant="primary"
                      onClick={handleUpdateQuiz}
                      disabled={isUpdatingQuiz}
                      isLoading={isUpdatingQuiz}
                    >
                      Save
                    </Button>
                    <Button
                      variant="secondary"
                      onClick={() => {
                        setIsEditingQuiz(false);
                        setQuizTitle(quiz.title);
                        setQuizPassingScore(quiz.passingScore);
                      }}
                      disabled={isUpdatingQuiz}
                    >
                      Cancel
                    </Button>
                  </div>
                </div>
              ) : (
                <>
                  <h1 className="text-4xl font-bold text-gray-100 mb-2">{quiz.title}</h1>
                  <p className="text-gray-400">Passing Score: {quiz.passingScore}%</p>
                </>
              )}
            </div>
            {!isEditingQuiz && (
              <div className="flex gap-2">
                <Button variant="secondary" onClick={() => setIsEditingQuiz(true)}>
                  <Edit className="w-4 h-4 mr-2" />
                  Edit Quiz
                </Button>
                <Button variant="danger" onClick={() => setShowDeleteQuizDialog(true)}>
                  <Trash2 className="w-4 h-4 mr-2" />
                  Delete Quiz
                </Button>
              </div>
            )}
          </div>
        </div>

        {/* Questions Section */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-gray-100">
              Questions ({quiz.questions.length})
            </h2>
            <Button variant="primary" onClick={openAddQuestionModal}>
              <Plus className="w-4 h-4 mr-2" />
              Add Question
            </Button>
          </div>

          {quiz.questions.length === 0 ? (
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center">
              <p className="text-gray-400 text-lg mb-4">No questions yet</p>
              <p className="text-gray-500 mb-6">Start building your quiz by adding questions</p>
              <Button variant="primary" onClick={openAddQuestionModal}>
                Add Your First Question
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              {quiz.questions.map((question, index) => (
                <div
                  key={question.id}
                  className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-gray-600 transition-colors"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <span className="text-gray-500 font-semibold">{index + 1}.</span>
                        <h3 className="text-gray-100 font-medium text-lg">{question.text}</h3>
                      </div>
                      <div className="flex items-center gap-4 text-sm">
                        <span className="text-purple-400">{getQuestionTypeLabel(question.type)}</span>
                        <span className="text-gray-500">|</span>
                        <span className="text-gray-400">Points: {question.points}</span>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Button
                        variant="ghost"
                        onClick={() => openEditQuestionModal(question)}
                        className="!p-2"
                      >
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        onClick={() => {
                          setQuestionToDelete(question);
                          setShowDeleteQuestionDialog(true);
                        }}
                        className="!p-2 !text-red-400 hover:!text-red-300"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>

                  {/* Answers */}
                  <div className="mt-4 space-y-2">
                    {question.answers.map((answer, aIndex) => (
                      <div key={aIndex} className="flex items-center gap-3 text-gray-300">
                        {question.type === 'SINGLE_CHOICE' ? (
                          <div
                            className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${
                              answer.correct
                                ? 'border-green-500 bg-green-500/20'
                                : 'border-gray-600'
                            }`}
                          >
                            {answer.correct && <div className="w-3 h-3 bg-green-500 rounded-full" />}
                          </div>
                        ) : (
                          <div
                            className={`w-5 h-5 rounded border-2 flex items-center justify-center ${
                              answer.correct
                                ? 'border-green-500 bg-green-500/20'
                                : 'border-gray-600'
                            }`}
                          >
                            {answer.correct && <Check className="w-4 h-4 text-green-500" />}
                          </div>
                        )}
                        <span>{answer.text}</span>
                        {answer.correct && (
                          <Check className="w-4 h-4 text-green-500 ml-auto" />
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Question Modal */}
      <Modal
        isOpen={showQuestionModal}
        onClose={() => setShowQuestionModal(false)}
        title={editingQuestion ? 'Edit Question' : 'Add New Question'}
        size="lg"
      >
        <div className="space-y-4">
          {/* Question Text */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Question Text <span className="text-red-400">*</span>
            </label>
            <textarea
              value={questionForm.text}
              onChange={(e) => setQuestionForm({ ...questionForm, text: e.target.value })}
              rows={3}
              className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
              placeholder="Enter your question..."
            />
          </div>

          {/* Question Type */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Type</label>
            <select
              value={questionForm.type}
              onChange={(e) => setQuestionForm({ ...questionForm, type: e.target.value as QuestionType })}
              className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
              disabled={!!editingQuestion}
            >
              <option value="SINGLE_CHOICE">Single Choice</option>
              <option value="MULTIPLE_CHOICE">Multiple Choice</option>
              <option value="TRUE_FALSE">True/False</option>
            </select>
          </div>

          {/* Points */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Points</label>
            <input
              type="number"
              value={questionForm.points}
              onChange={(e) => setQuestionForm({ ...questionForm, points: parseInt(e.target.value) || 1 })}
              min={1}
              className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
          </div>

          {/* Answers */}
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Answers <span className="text-red-400">*</span>
            </label>
            <div className="space-y-3">
              {questionForm.answers.map((answer, index) => (
                <div key={index} className="flex items-start gap-3">
                  <div className="flex items-center h-10">
                    <input
                      type={questionForm.type === 'SINGLE_CHOICE' ? 'radio' : 'checkbox'}
                      name="correct"
                      checked={answer.correct}
                      onChange={(e) => updateAnswer(index, 'correct', e.target.checked)}
                      className="w-4 h-4 text-purple-600 focus:ring-purple-500"
                    />
                  </div>
                  <input
                    type="text"
                    value={answer.text}
                    onChange={(e) => updateAnswer(index, 'text', e.target.value)}
                    placeholder={`Answer ${index + 1}`}
                    className="flex-1 px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  />
                  {questionForm.answers.length > 2 && questionForm.type !== 'TRUE_FALSE' && (
                    <Button
                      variant="ghost"
                      onClick={() => removeAnswer(index)}
                      className="!p-2 !text-red-400 hover:!text-red-300"
                    >
                      <X className="w-4 h-4" />
                    </Button>
                  )}
                </div>
              ))}
            </div>
            {questionForm.type !== 'TRUE_FALSE' && (
              <Button variant="secondary" onClick={addAnswer} className="mt-3">
                <Plus className="w-4 h-4 mr-2" />
                Add Answer
              </Button>
            )}
            <p className="text-xs text-gray-400 mt-2">
              {questionForm.type === 'SINGLE_CHOICE' && 'Select exactly 1 correct answer'}
              {questionForm.type === 'MULTIPLE_CHOICE' && 'Select at least 1 correct answer'}
              {questionForm.type === 'TRUE_FALSE' && 'Select exactly 1 correct answer'}
            </p>
          </div>

          {/* Actions */}
          <div className="flex gap-3 justify-end pt-4">
            <Button
              variant="secondary"
              onClick={() => setShowQuestionModal(false)}
              disabled={isSavingQuestion}
            >
              Cancel
            </Button>
            <Button
              variant="primary"
              onClick={handleSaveQuestion}
              disabled={isSavingQuestion}
              isLoading={isSavingQuestion}
            >
              {isSavingQuestion ? 'Saving...' : editingQuestion ? 'Update Question' : 'Add Question'}
            </Button>
          </div>
        </div>
      </Modal>

      {/* Delete Quiz Confirmation */}
      <ConfirmDialog
        isOpen={showDeleteQuizDialog}
        onClose={() => setShowDeleteQuizDialog(false)}
        onConfirm={handleDeleteQuiz}
        title="Delete Quiz"
        message={`Are you sure you want to delete "${quiz.title}"? This will also delete all questions. This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeletingQuiz}
      />

      {/* Delete Question Confirmation */}
      <ConfirmDialog
        isOpen={showDeleteQuestionDialog}
        onClose={() => {
          setShowDeleteQuestionDialog(false);
          setQuestionToDelete(null);
        }}
        onConfirm={handleDeleteQuestion}
        title="Delete Question"
        message={`Are you sure you want to delete this question? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        isLoading={isDeletingQuestion}
      />
    </div>
  );
}
