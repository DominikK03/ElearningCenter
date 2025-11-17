import apiClient from './api';
import type { AdminStats } from '../types/api';

interface AdjustBalancePayload {
  amount: number;
  type: 'CREDIT' | 'DEBIT';
  reason?: string;
}

export const getAdminStats = async (): Promise<AdminStats> => {
  const response = await apiClient.get<AdminStats>('/admin/stats');
  return response.data;
};

export const adjustUserBalance = async (
  userId: number,
  data: AdjustBalancePayload
): Promise<void> => {
  await apiClient.post(`/admin/users/${userId}/balance-adjustments`, data);
};
