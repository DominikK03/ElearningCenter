import apiClient from './api';
import type { AckResponse } from '../types/api';

export const uploadMaterial = async (
  courseId: number,
  sectionId: number,
  lessonId: number,
  title: string,
  file: File
): Promise<AckResponse> => {
  const formData = new FormData();
  formData.append('title', title);
  formData.append('file', file);

  const response = await apiClient.post<AckResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons/${lessonId}/materials`,
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }
  );

  return response.data;
};

export const addLinkMaterial = async (
  courseId: number,
  sectionId: number,
  lessonId: number,
  title: string,
  url: string
): Promise<AckResponse> => {
  const params = new URLSearchParams();
  params.append('title', title);
  params.append('url', url);

  const response = await apiClient.post<AckResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons/${lessonId}/materials/link`,
    params,
    {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    }
  );

  return response.data;
};

export const deleteMaterial = async (
  courseId: number,
  sectionId: number,
  lessonId: number,
  materialId: number
): Promise<AckResponse> => {
  const response = await apiClient.delete<AckResponse>(
    `/courses/${courseId}/sections/${sectionId}/lessons/${lessonId}/materials/${materialId}`
  );

  return response.data;
};
