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
import BalancePage from './pages/dashboard/BalancePage';
import ProfilePage from './pages/profile/ProfilePage';
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import AdminCoursesPage from './pages/admin/AdminCoursesPage';
import CreateCoursePage from './pages/instructor/CreateCoursePage';
import EditCoursePage from './pages/instructor/EditCoursePage';
import ManageCoursePage from './pages/instructor/ManageCoursePage';
import MyCoursesPage from './pages/instructor/MyCoursesPage';
import MyEnrollmentsPage from './pages/student/MyEnrollmentsPage';
import StudentCourseViewPage from './pages/student/StudentCourseViewPage';
import CreateQuizPage from './pages/instructor/CreateQuizPage';
import ManageQuizPage from './pages/instructor/ManageQuizPage';
import TakeQuizPage from './pages/student/TakeQuizPage';
import QuizResultPage from './pages/student/QuizResultPage';
import QuizAttemptsPage from './pages/student/QuizAttemptsPage';
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
            <Route path="/balance" element={<BalancePage />} />

            <Route path="/my-courses" element={
              <ProtectedRoute allowedRoles={['STUDENT']}>
                <MyEnrollmentsPage />
              </ProtectedRoute>
            } />
            <Route path="/dashboard/course/:id" element={
              <ProtectedRoute allowedRoles={['STUDENT']}>
                <StudentCourseViewPage />
              </ProtectedRoute>
            } />

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

            {/* Quiz Routes - Instructor */}
            <Route path="/instructor/quiz/create" element={
              <ProtectedRoute allowedRoles={['INSTRUCTOR', 'ADMIN']}>
                <CreateQuizPage />
              </ProtectedRoute>
            } />
            <Route path="/instructor/quiz/:id/manage" element={
              <ProtectedRoute allowedRoles={['INSTRUCTOR', 'ADMIN']}>
                <ManageQuizPage />
              </ProtectedRoute>
            } />

            {/* Quiz Routes - Student */}
            <Route path="/quiz/:id/take" element={
              <ProtectedRoute allowedRoles={['STUDENT']}>
                <TakeQuizPage />
              </ProtectedRoute>
            } />
            <Route path="/quiz/:quizId/result/:attemptId" element={
              <ProtectedRoute allowedRoles={['STUDENT']}>
                <QuizResultPage />
              </ProtectedRoute>
            } />
            <Route path="/quiz/:id/attempts" element={
              <ProtectedRoute allowedRoles={['STUDENT']}>
                <QuizAttemptsPage />
              </ProtectedRoute>
            } />

            <Route path="/admin" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboardPage />
              </ProtectedRoute>
            } />
            <Route path="/admin/users" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminUsersPage />
              </ProtectedRoute>
            } />
            <Route path="/admin/courses" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminCoursesPage />
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
