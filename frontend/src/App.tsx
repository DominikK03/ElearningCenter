import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';

import HomePage from './pages/courses/HomePage';
import CoursesPage from './pages/courses/CoursesPage';
import CourseDetailsPage from './pages/courses/CourseDetailsPage';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import VerifyEmailPage from './pages/auth/VerifyEmailPage';
import ResendVerificationPage from './pages/auth/ResendVerificationPage';
import RequestPasswordResetPage from './pages/auth/RequestPasswordResetPage';
import ResetPasswordPage from './pages/auth/ResetPasswordPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import ProfilePage from './pages/profile/ProfilePage';
import AdminPanelPage from './pages/admin/AdminPanelPage';
import CreateCoursePage from './pages/instructor/CreateCoursePage';
import EditCoursePage from './pages/instructor/EditCoursePage';
import ManageCoursePage from './pages/instructor/ManageCoursePage';
import MyCoursesPage from './pages/instructor/MyCoursesPage';
import NotFoundPage from './pages/error/NotFoundPage';
import ForbiddenPage from './pages/error/ForbiddenPage';
import ProtectedRoute from './components/auth/ProtectedRoute';
import DashboardLayout from './layouts/DashboardLayout';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomePage />} />

          {/* Public Course Routes */}
          <Route path="/courses" element={<CoursesPage />} />
          <Route path="/courses/:id" element={<CourseDetailsPage />} />

          {/* Auth Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/verify-email" element={<VerifyEmailPage />} />
          <Route path="/resend-verification" element={<ResendVerificationPage />} />
          <Route path="/request-password-reset" element={<RequestPasswordResetPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />

          {/* Protected Routes */}
          <Route element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/profile" element={<ProfilePage />} />

            <Route path="/my-courses" element={<div>My Courses - Coming Soon</div>} />

            <Route path="/my-teaching" element={
              <ProtectedRoute allowedRoles={['INSTRUCTOR', 'ADMIN']}>
                <MyCoursesPage />
              </ProtectedRoute>
            } />
            <Route path="/create-course" element={
              <ProtectedRoute allowedRoles={['INSTRUCTOR', 'ADMIN']}>
                <CreateCoursePage />
              </ProtectedRoute>
            } />
            <Route path="/courses/:id/edit" element={
              <ProtectedRoute allowedRoles={['INSTRUCTOR', 'ADMIN']}>
                <EditCoursePage />
              </ProtectedRoute>
            } />
            <Route path="/dashboard/course/:id/manage" element={
              <ProtectedRoute allowedRoles={['INSTRUCTOR', 'ADMIN']}>
                <ManageCoursePage />
              </ProtectedRoute>
            } />

            <Route path="/admin" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminPanelPage />
              </ProtectedRoute>
            } />
            <Route path="/admin/users" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <div>User Management - Coming Soon</div>
              </ProtectedRoute>
            } />
            <Route path="/admin/courses" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <div>Courses Management - Coming Soon</div>
              </ProtectedRoute>
            } />
            <Route path="/admin/reports" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <div>Reports - Coming Soon</div>
              </ProtectedRoute>
            } />
          </Route>

          {/* Error Pages */}
          <Route path="/forbidden" element={<ForbiddenPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
