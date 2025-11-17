import apiClient from './api';
import type {
  ApiResponse,
  AckResponse,
  PublicCourseDetails,
  FullCourseDetails,
  CreateCourseRequest,
  UpdateCourseRequest,
  PublishCourseResponse,
  AddSectionRequest,
  UpdateSectionRequest,
  AddLessonRequest,
  UpdateLessonRequest,
  PagedCoursesResponse,
  PagedPublicCoursesResponse,
  PageRequest,
  CourseLevel,
} from '../types/api';

// ============================================
// Course Management
// ============================================

export const createCourse = async (data: CreateCourseRequest): Promise<AckResponse> => {
  const response = await apiClient.post<AckResponse>('/courses', data);
  return response.data;
};

export const getAllCourses = async (
  params?: PageRequest & { category?: string; level?: CourseLevel }
): Promise<PagedCoursesResponse> => {
  const response = await apiClient.get<PagedCoursesResponse>('/courses', { params });
  return response.data;
};

export const getPublishedCourses = async (
  params?: PageRequest & { category?: string; level?: CourseLevel }
): Promise<PagedPublicCoursesResponse> => {
  const response = await apiClient.get<PagedPublicCoursesResponse>('/courses/published', {
    params,
  });
  return response.data;
};

export const getCourseById = async (courseId: number): Promise<PublicCourseDetails> => {
  const response = await apiClient.get<PublicCourseDetails>(`/courses/${courseId}`);
  return response.data;
};

export const getFullCourseDetails = async (courseId: number): Promise<FullCourseDetails> => {
  const response = await apiClient.get<FullCourseDetails>(`/courses/${courseId}/full`);
  return response.data;
};

export const getCoursesByInstructor = async (
  instructorId: number,
  params?: PageRequest
): Promise<PagedCoursesResponse> => {
  const response = await apiClient.get<PagedCoursesResponse>(
    `/courses/instructor/${instructorId}`,
    { params }
  );
  return response.data;
};

export const getAllCategories = async (): Promise<string[]> => {
  const response = await apiClient.get<string[]>('/courses/categories');
  return response.data;
};

export const updateCourse = async (
  courseId: number,
  data: UpdateCourseRequest
): Promise<AckResponse> => {
  const response = await apiClient.put<AckResponse>(`/courses/${courseId}/update`, data);
  return response.data;
};

export const publishCourse = async (courseId: number): Promise<PublishCourseResponse> => {
  const response = await apiClient.post<PublishCourseResponse>(`/courses/${courseId}/publish`);
  return response.data;
};

export const unpublishCourse = async (courseId: number, reason?: string): Promise<AckResponse> => {
  const response = await apiClient.post<AckResponse>(
    `/courses/${courseId}/unpublish`,
    reason ? { reason } : undefined
  );
  return response.data;
};

export const deleteCourse = async (courseId: number, reason?: string): Promise<AckResponse> => {
  const config = reason ? { data: { reason } } : undefined;
  const response = await apiClient.delete<AckResponse>(`/courses/${courseId}`, config);
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

// ============================================
// Batch Order Updates
// ============================================

export const updateSectionsOrder = async (
  courseId: number,
  sectionOrderMap: Record<number, number>
): Promise<ApiResponse> => {
  const response = await apiClient.patch<ApiResponse>(
    `/courses/${courseId}/sections/order`,
    { sectionOrderMap }
  );
  return response.data;
};

export const updateLessonsOrder = async (
  courseId: number,
  sectionId: number,
  lessonOrderMap: Record<number, number>
): Promise<ApiResponse> => {
  const response = await apiClient.patch<ApiResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons/order`,
    { lessonOrderMap }
  );
  return response.data;
};
