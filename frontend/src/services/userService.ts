import apiClient from './api';
import type {
  ApiResponse,
  User,
  RegisterRequest,
  LoginRequest,
  UpdateProfileRequest,
  ChangePasswordRequest,
  VerifyEmailRequest,
  ResendVerificationRequest,
  RequestPasswordResetRequest,
  ResetPasswordRequest,
  AddBalanceRequest,
  PageResponse,
  PageRequest,
} from '../types/api';

// ============================================
// Authentication
// ============================================

export const register = async (data: RegisterRequest): Promise<ApiResponse<User>> => {
  const response = await apiClient.post<ApiResponse<User>>('/users/register', data);
  return response.data;
};

export const login = async (data: LoginRequest): Promise<ApiResponse<User>> => {
  const response = await apiClient.post<ApiResponse<User>>('/users/login', data);
  return response.data;
};

export const logout = async (): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>('/users/logout');
  return response.data;
};

// ============================================
// Email Verification
// ============================================

export const verifyEmail = async (data: VerifyEmailRequest): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>('/users/verify-email', data);
  return response.data;
};

export const resendVerification = async (data: ResendVerificationRequest): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>('/users/resend-verification', data);
  return response.data;
};

// ============================================
// Password Reset
// ============================================

export const requestPasswordReset = async (
  data: RequestPasswordResetRequest
): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>('/users/request-password-reset', data);
  return response.data;
};

export const resetPassword = async (data: ResetPasswordRequest): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>('/users/reset-password', data);
  return response.data;
};

// ============================================
// User Profile
// ============================================

export const getCurrentUser = async (): Promise<ApiResponse<User>> => {
  const response = await apiClient.get<ApiResponse<User>>('/users/me');
  return response.data;
};

export const getUserById = async (userId: number): Promise<ApiResponse<User>> => {
  const response = await apiClient.get<ApiResponse<User>>(`/users/${userId}`);
  return response.data;
};

export const updateProfile = async (
  userId: number,
  data: UpdateProfileRequest
): Promise<ApiResponse<User>> => {
  const response = await apiClient.put<ApiResponse<User>>(`/users/${userId}/profile`, data);
  return response.data;
};

export const changePassword = async (
  userId: number,
  data: ChangePasswordRequest
): Promise<ApiResponse> => {
  const response = await apiClient.put<ApiResponse>(`/users/${userId}/password`, data);
  return response.data;
};

// ============================================
// User Management (Admin)
// ============================================

export const getAllUsers = async (
  params?: PageRequest & { role?: string }
): Promise<ApiResponse<PageResponse<User>>> => {
  const response = await apiClient.get<ApiResponse<PageResponse<User>>>('/users', { params });
  return response.data;
};

export const enableUser = async (userId: number): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/users/${userId}/enable`);
  return response.data;
};

export const disableUser = async (userId: number): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/users/${userId}/disable`);
  return response.data;
};

export const addBalance = async (userId: number, data: AddBalanceRequest): Promise<ApiResponse> => {
  const response = await apiClient.post<ApiResponse>(`/users/${userId}/balance/add`, data);
  return response.data;
};