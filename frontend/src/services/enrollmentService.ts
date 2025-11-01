import apiClient from './api';
import type { ApiResponse, Enrollment, EnrollRequest } from '../types/api';

// ============================================
// Enrollment Management
// ============================================

export const enrollInCourse = async (data: EnrollRequest): Promise<ApiResponse<Enrollment>> => {
  const response = await apiClient.post<ApiResponse<Enrollment>>('/enrollments', data);
  return response.data;
};

export const getStudentEnrollments = async (
  studentId: number
): Promise<Enrollment[]> => {
  const response = await apiClient.get<Enrollment[]>(
    `/enrollments/student/${studentId}`
  );
  return response.data;
};

export const getCourseEnrollments = async (courseId: number): Promise<ApiResponse<Enrollment[]>> => {
  const response = await apiClient.get<ApiResponse<Enrollment[]>>(
    `/enrollments/course/${courseId}`
  );
  return response.data;
};

export const unenrollFromCourse = async (enrollmentId: number): Promise<ApiResponse> => {
  const response = await apiClient.delete<ApiResponse>(`/enrollments/${enrollmentId}`);
  return response.data;
};

// ============================================
// Lesson Progress
// ============================================

export const markLessonAsCompleted = async (
  enrollmentId: number,
  sectionId: number,
  lessonId: number
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(
    `/enrollments/${enrollmentId}/sections/${sectionId}/lessons/${lessonId}/complete`
  );
  return response.data;
};

export const getCompletedLessons = async (
  enrollmentId: number
): Promise<number[]> => {
  const response = await apiClient.get<number[]>(
    `/enrollments/${enrollmentId}/completed-lessons`
  );
  return response.data;
};