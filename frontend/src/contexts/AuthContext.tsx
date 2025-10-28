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

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<ApiErrorDetails | null>(null);

  const isAuthenticated = user !== null;

  const checkAuth = async () => {
    try {
      setLoading(true);
      setError(null);

      const apiResponse = await userService.getCurrentUser();
      const userData = (apiResponse as unknown as { data?: User }).data || apiResponse;

      if (userData && typeof userData === 'object' && 'id' in userData) {
        setUser(userData as User);
      }

      setLoading(false);
    } catch (err) {
      const error = err as { response?: { status?: number } };
      if (error.response?.status === 403 || error.response?.status === 401) {
        setUser(null);
        setLoading(false);
        return;
      }

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

      await userService.login(credentials);
      await checkAuth();
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

export { AuthContext, AuthProvider };
