import axios from 'axios';
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import type { ApiResponse, AuthenticationResponse, RefreshTokenRequest } from '../types/api';
import { tokenStorage } from '../utils/tokenStorage';

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
});

let isRefreshing = false;
let refreshSubscribers: Array<(token: string) => void> = [];

const onRefreshed = (token: string) => {
  refreshSubscribers.forEach((callback) => callback(token));
  refreshSubscribers = [];
};

const addRefreshSubscriber = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback);
};

// ============================================
// Request Interceptor - Add JWT Token
// ============================================

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = tokenStorage.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// ============================================
// Response Interceptor - Handle Token Refresh
// ============================================

apiClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    return response;
  },
  async (error: AxiosError<ApiResponse>) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // If error is 401 and we haven't retried yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Skip refresh for login/register endpoints
      if (
        originalRequest.url?.includes('/login') ||
        originalRequest.url?.includes('/register') ||
        originalRequest.url?.includes('/refresh')
      ) {
        return Promise.reject(error);
      }

      if (isRefreshing) {
        // Wait for the token to be refreshed
        return new Promise((resolve) => {
          addRefreshSubscriber((token: string) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            resolve(apiClient(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = tokenStorage.getRefreshToken();
      if (!refreshToken) {
        tokenStorage.clearTokens();
        isRefreshing = false;
        return Promise.reject(error);
      }

      try {
        const response = await axios.post<AuthenticationResponse>(
          `${API_BASE_URL}/auth/refresh`,
          { refreshToken } as RefreshTokenRequest
        );

        const { accessToken, refreshToken: newRefreshToken } = response.data;
        tokenStorage.setAccessToken(accessToken);
        tokenStorage.setRefreshToken(newRefreshToken);

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }

        onRefreshed(accessToken);
        isRefreshing = false;

        return apiClient(originalRequest);
      } catch (refreshError) {
        tokenStorage.clearTokens();
        isRefreshing = false;
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
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