import { createContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { userService, handleApiError } from '../services';
import type { User, LoginRequest, RegisterRequest } from '../types/api';
import type { ApiErrorDetails } from '../services/api';


interface AuthContextType {
  user: User | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  checkAuth: () => Promise<void>;
  error: ApiErrorDetails | null;
  clearError: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<ApiErrorDetails | null>(null);

  const isAuthenticated = user !== null;

  const checkAuth = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await userService.getCurrentUser();

      if (response.data) {
        setUser(response.data);
      }

      setLoading(false);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError);
      setUser(null);
      setLoading(false);
    }
  };

  const login = async (credentials: LoginRequest) => {
    try {
      setLoading(true);
      setError(null);

      const response = await userService.login(credentials);

      if (response.data) {
        setUser(response.data);
      }

      setLoading(false);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError);
      setLoading(false);
      throw err;
    }
  };

  const logout = async () => {
    try {
      setLoading(true);
      setError(null);

      await userService.logout();
      setUser(null);

      setLoading(false);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError);
      setUser(null);
      setLoading(false);
    }
  };

  const register = async (data: RegisterRequest) => {
    try {
      setLoading(true);
      setError(null);

      await userService.register(data);

      // Note: After registration, user is NOT automatically logged in
      // They need to be enabled by admin first
      // So we don't set the user here

      setLoading(false);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError);
      setLoading(false);
      throw err;
    }
  };

  const clearError = () => {
    setError(null);
  };

  useEffect(() => {
    checkAuth();
  }, []);

  const value: AuthContextType = {
    user,
    loading,
    isAuthenticated,
    login,
    logout,
    register,
    checkAuth,
    error,
    clearError,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
