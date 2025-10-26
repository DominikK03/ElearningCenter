import axios from 'axios';
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import type { ApiResponse } from '../types/api';

// ============================================
// Axios Instance Configuration
// ============================================

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// ============================================
// Request Interceptor
// ============================================

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// ============================================
// Response Interceptor
// ============================================

apiClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    return response;
  },
  (error: AxiosError<ApiResponse>) => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
      }

      if (status === 403) {
        console.error('Access denied:', data.message);
      }

      if (status === 404) {
        console.error('Resource not found:', data.message);
      }

      if (status === 500) {
        console.error('Server error:', data.message);
      }

      return Promise.reject(error);
    }

    if (error.code === 'ECONNABORTED') {
      console.error('Request timeout');
    } else if (!error.response) {
      console.error('Network error - please check your connection');
    }

    return Promise.reject(error);
  }
);

export interface ApiErrorDetails {
  message: string;
  status?: number;
  errors?: Record<string, string>;
}

export const handleApiError = (error: unknown): ApiErrorDetails => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ApiResponse>;

    if (axiosError.response?.data) {
      return {
        message: axiosError.response.data.message || 'An error occurred',
        status: axiosError.response.status,
        errors: axiosError.response.data.errors,
      };
    }

    if (axiosError.code === 'ECONNABORTED') {
      return {
        message: 'Request timeout - please try again',
      };
    }

    if (!axiosError.response) {
      return {
        message: 'Network error - please check your internet connection',
      };
    }
  }

  return {
    message: 'An unexpected error occurred',
  };
};

export default apiClient;