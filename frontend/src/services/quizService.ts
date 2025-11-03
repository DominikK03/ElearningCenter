import apiClient from './api';
import type {
  ApiResponse,
  Quiz,
  CreateQuizRequest,
  UpdateQuizRequest,
  AddQuestionRequest,
  UpdateQuestionRequest,
  SubmitQuizAttemptRequest,
  QuizAttempt,
} from '../types/api';

// ============================================
// Quiz Management (Instructor)
// ============================================

export const createQuiz = async (data: CreateQuizRequest): Promise<ApiResponse<Quiz>> => {
  const response = await apiClient.post<ApiResponse<Quiz>>('/quizzes', data);
  return response.data;
};

export const getQuizDetails = async (quizId: number): Promise<Quiz> => {
  const response = await apiClient.get<Quiz>(`/quizzes/${quizId}`);
  return response.data;
};

export const updateQuiz = async (
  quizId: number,
  data: UpdateQuizRequest
): Promise<ApiResponse<Quiz>> => {
  const response = await apiClient.put<ApiResponse<Quiz>>(`/quizzes/${quizId}`, data);
  return response.data;
};

export const deleteQuiz = async (quizId: number): Promise<ApiResponse> => {
  const response = await apiClient.delete<ApiResponse>(`/quizzes/${quizId}`);
  return response.data;
};

export const getCourseQuizzes = async (courseId: number): Promise<Quiz[]> => {
  const response = await apiClient.get<Quiz[]>(`/quizzes/course/${courseId}`);
  return response.data;
};

// ============================================
// Question Management (Instructor)
// ============================================

export const addQuestion = async (
  quizId: number,
  data: AddQuestionRequest
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/quizzes/${quizId}/questions`, data);
  return response.data;
};

export const updateQuestion = async (
  quizId: number,
  questionId: number,
  data: UpdateQuestionRequest
): Promise<ApiResponse> => {
  const response = await apiClient.put<ApiResponse>(
    `/quizzes/${quizId}/questions/${questionId}`,
    data
  );
  return response.data;
};

export const deleteQuestion = async (quizId: number, questionId: number): Promise<ApiResponse> => {
  const response = await apiClient.delete<ApiResponse>(
    `/quizzes/${quizId}/questions/${questionId}`
  );
  return response.data;
};

// ============================================
// Quiz Taking (Student)
// ============================================

export const getQuizForStudent = async (quizId: number): Promise<Quiz> => {
  const response = await apiClient.get<Quiz>(`/quizzes/${quizId}/take`);
  return response.data;
};

export const submitQuizAttempt = async (
  quizId: number,
  data: SubmitQuizAttemptRequest
): Promise<QuizAttempt> => {
  const response = await apiClient.post<QuizAttempt>(
    `/quizzes/${quizId}/submit`,
    data
  );
  return response.data;
};

export const getStudentQuizAttempts = async (
  quizId: number
): Promise<QuizAttempt[]> => {
  const response = await apiClient.get<{ attempts: QuizAttempt[] }>(`/quizzes/${quizId}/attempts`);
  return response.data.attempts;
};

export const getBestQuizAttempt = async (quizId: number): Promise<QuizAttempt | null> => {
  const response = await apiClient.get<QuizAttempt>(
    `/quizzes/${quizId}/attempts/best`
  );
  return response.data;
};

export const getQuizAttemptDetails = async (
  quizId: number,
  attemptId: number
): Promise<QuizAttempt> => {
  const response = await apiClient.get<QuizAttempt>(
    `/quizzes/${quizId}/attempts/${attemptId}`
  );
  return response.data;
};
