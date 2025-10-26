import apiClient from './api';
import type {
  ApiResponse,
  Course,
  CreateCourseRequest,
  UpdateCourseRequest,
  AddSectionRequest,
  UpdateSectionRequest,
  AddLessonRequest,
  UpdateLessonRequest,
  PageResponse,
  PageRequest,
  CourseLevel,
} from '../types/api';

// ============================================
// Course Management
// ============================================

export const createCourse = async (data: CreateCourseRequest): Promise<ApiResponse<Course>> => {
  const response = await apiClient.post<ApiResponse<Course>>('/courses', data);
  return response.data;
};

export const getAllCourses = async (
  params?: PageRequest & { category?: string; level?: CourseLevel }
): Promise<ApiResponse<PageResponse<Course>>> => {
  const response = await apiClient.get<ApiResponse<PageResponse<Course>>>('/courses', { params });
  return response.data;
};

export const getPublishedCourses = async (
  params?: PageRequest & { category?: string; level?: CourseLevel }
): Promise<ApiResponse<PageResponse<Course>>> => {
  const response = await apiClient.get<ApiResponse<PageResponse<Course>>>('/courses/published', {
    params,
  });
  return response.data;
};

export const getCourseById = async (courseId: number): Promise<ApiResponse<Course>> => {
  const response = await apiClient.get<ApiResponse<Course>>(`/courses/${courseId}`);
  return response.data;
};

export const getCoursesByInstructor = async (
  instructorId: number,
  params?: PageRequest
): Promise<ApiResponse<PageResponse<Course>>> => {
  const response = await apiClient.get<ApiResponse<PageResponse<Course>>>(
    `/courses/instructor/${instructorId}`,
    { params }
  );
  return response.data;
};

export const updateCourse = async (
  courseId: number,
  data: UpdateCourseRequest
): Promise<ApiResponse<Course>> => {
  const response = await apiClient.put<ApiResponse<Course>>(`/courses/${courseId}/update`, data);
  return response.data;
};

export const publishCourse = async (courseId: number): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/courses/${courseId}/publish`);
  return response.data;
};

export const unpublishCourse = async (courseId: number): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/courses/${courseId}/unpublish`);
  return response.data;
};

export const deleteCourse = async (courseId: number): Promise<ApiResponse> => {
  const response = await apiClient.delete<ApiResponse>(`/courses/${courseId}`);
  return response.data;
};

// ============================================
// Section Management
// ============================================

export const addSection = async (
  courseId: number,
  data: AddSectionRequest
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/courses/${courseId}/sections`, data);
  return response.data;
};

export const updateSection = async (
  courseId: number,
  sectionId: number,
  data: UpdateSectionRequest
): Promise<ApiResponse> => {
  const response = await apiClient.put<ApiResponse>(
    `/courses/${courseId}/sections/${sectionId}`,
    data
  );
  return response.data;
};

export const deleteSection = async (courseId: number, sectionId: number): Promise<ApiResponse> => {
  const response = await apiClient.delete<ApiResponse>(`/courses/${courseId}/sections/${sectionId}`);
  return response.data;
};

// ============================================
// Lesson Management
// ============================================

export const addLesson = async (
  courseId: number,
  sectionId: number,
  data: AddLessonRequest
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons`,
    data
  );
  return response.data;
};

export const updateLesson = async (
  courseId: number,
  sectionId: number,
  lessonId: number,
  data: UpdateLessonRequest
): Promise<ApiResponse> => {
  const response = await apiClient.put<ApiResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons/${lessonId}`,
    data
  );
  return response.data;
};

export const deleteLesson = async (
  courseId: number,
  sectionId: number,
  lessonId: number
): Promise<ApiResponse> => {
  const response = await apiClient.delete<ApiResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons/${lessonId}`
  );
  return response.data;
};