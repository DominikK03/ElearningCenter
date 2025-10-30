// ============================================
// API Response Types
// ============================================

export interface ApiResponse<T = never> {
  status: number;
  message?: string;
  data?: T;
  errors?: Record<string, string>;
  timestamp?: string;
}

// ============================================
// User Types
// ============================================

export type UserRole = 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';

export const UserRole = {
  STUDENT: 'STUDENT' as const,
  INSTRUCTOR: 'INSTRUCTOR' as const,
  ADMIN: 'ADMIN' as const,
};

export interface User {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  enabled: boolean;
  emailVerified: boolean;
  balance: number;
  createdAt: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UpdateProfileRequest {
  username?: string;
  email?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface VerifyEmailRequest {
  token: string;
}

export interface ResendVerificationRequest {
  email: string;
}

export interface RequestPasswordResetRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface AddBalanceRequest {
  amount: number;
}

// ============================================
// Course Types
// ============================================

export type CourseLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export const CourseLevel = {
  BEGINNER: 'BEGINNER' as const,
  INTERMEDIATE: 'INTERMEDIATE' as const,
  ADVANCED: 'ADVANCED' as const,
};

export interface Material {
  id: number;
  title: string;
  url: string;
  type: string;
}

export interface Lesson {
  id: number;
  title: string;
  content: string;
  videoUrl?: string;
  durationMinutes?: number;
  orderIndex: number;
  materials: Material[];
}

export interface Section {
  id: number;
  title: string;
  orderIndex: number;
  lessonsCount: number;
  lessons: Lesson[];
}

export interface PublicSection {
  id: number;
  title: string;
  orderIndex: number;
}

export interface PublicCourse {
  id: number;
  title: string;
  description: string;
  price: number;
  currency: string;
  thumbnailUrl?: string;
  category: string;
  level: CourseLevel;
  instructorName: string;
  published: boolean;
  createdAt: string;
  sectionsCount: number;
  totalLessonsCount: number;
}

export interface PublicCourseDetails {
  id: number;
  title: string;
  description: string;
  price: number;
  currency: string;
  thumbnailUrl?: string;
  category: string;
  level: CourseLevel;
  instructorName: string;
  published: boolean;
  createdAt: string;
  sections: PublicSection[];
  sectionsCount: number;
  totalLessonsCount: number;
}

export interface Course {
  id: number;
  title: string;
  description: string;
  price: number;
  currency: string;
  thumbnailUrl?: string;
  category: string;
  level: CourseLevel;
  instructorId: number;
  published: boolean;
  createdAt: string;
  sections: Section[];
  sectionsCount: number;
  totalLessonsCount: number;
}

export interface CreateCourseRequest {
  title: string;
  description: string;
  price: number;
  category: string;
  level: CourseLevel;
  thumbnailUrl?: string;
}

export interface UpdateCourseRequest {
  title?: string;
  description?: string;
  price?: number;
  category?: string;
  level?: CourseLevel;
  thumbnailUrl?: string;
}

export interface AddSectionRequest {
  title: string;
  orderIndex: number;
}

export interface UpdateSectionRequest {
  title?: string;
  orderIndex?: number;
}

export interface AddLessonRequest {
  title: string;
  content?: string;
  videoUrl?: string;
  durationMinutes?: number;
  orderIndex: number;
}

export interface UpdateLessonRequest {
  title?: string;
  content?: string;
  videoUrl?: string;
  durationMinutes?: number;
  orderIndex?: number;
}

// ============================================
// Enrollment Types
// ============================================

export type EnrollmentStatus = 'ACTIVE' | 'COMPLETED' | 'DROPPED';

export const EnrollmentStatus = {
  ACTIVE: 'ACTIVE' as const,
  COMPLETED: 'COMPLETED' as const,
  DROPPED: 'DROPPED' as const,
};

export interface Enrollment {
  id: number;
  studentId: number;
  courseId: number;
  progress: number;
  status: EnrollmentStatus;
  enrolledAt: string;
  completedAt?: string;
}

export interface EnrollRequest {
  courseId: number;
}

// ============================================
// Quiz Types
// ============================================

export type QuestionType = 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' | 'TRUE_FALSE';

export const QuestionType = {
  SINGLE_CHOICE: 'SINGLE_CHOICE' as const,
  MULTIPLE_CHOICE: 'MULTIPLE_CHOICE' as const,
  TRUE_FALSE: 'TRUE_FALSE' as const,
};

export interface Answer {
  text: string;
  correct?: boolean;
  index: number;
}

export interface Question {
  id: number;
  text: string;
  type: QuestionType;
  points: number;
  orderIndex: number;
  answers: Answer[];
}

export interface Quiz {
  id: number;
  title: string;
  passingScore: number;
  lessonId?: number;
  instructorId: number;
  createdAt: string;
  questions: Question[];
  questionsCount: number;
}

export interface CreateQuizRequest {
  title: string;
  passingScore: number;
  lessonId?: number;
}

export interface UpdateQuizRequest {
  title?: string;
  passingScore?: number;
}

export interface AnswerRequest {
  text: string;
  correct: boolean;
}

export interface AddQuestionRequest {
  text: string;
  type: QuestionType;
  points: number;
  orderIndex: number;
  answers: AnswerRequest[];
}

export interface UpdateQuestionRequest {
  text?: string;
  points?: number;
  orderIndex?: number;
  answers?: AnswerRequest[];
}

export interface StudentAnswer {
  questionId: number;
  selectedAnswerIndexes: number[];
}

export interface SubmitQuizAttemptRequest {
  answers: StudentAnswer[];
}

export interface QuizAttempt {
  id: number;
  quizId: number;
  studentId: number;
  score: number;
  maxScore: number;
  passed: boolean;
  scorePercentage: number;
  attemptedAt: string;
  answers?: StudentAnswer[];
}

// ============================================
// Pagination Types
// ============================================

export interface PageRequest {
  page?: number;
  size?: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface PagedCoursesResponse {
  courses: PublicCourse[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}