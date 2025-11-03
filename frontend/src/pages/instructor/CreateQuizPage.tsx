import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import Button from '../../components/ui/Button';
import { createQuiz } from '../../services/quizService';
import { getFullCourseDetails } from '../../services/courseService';
import type { CreateQuizRequest, FullCourseDetails, Lesson, Section } from '../../types/api';

type AssignmentType = 'none' | 'course' | 'section' | 'lesson';

export default function CreateQuizPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const courseId = searchParams.get('courseId');
  const sectionId = searchParams.get('sectionId');
  const lessonId = searchParams.get('lessonId');

  const [course, setCourse] = useState<FullCourseDetails | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isCreating, setIsCreating] = useState(false);

  const [title, setTitle] = useState('');
  const [passingScore, setPassingScore] = useState(70);
  const [assignmentType, setAssignmentType] = useState<AssignmentType>('none');
  const [selectedCourseId, setSelectedCourseId] = useState<number | undefined>(undefined);
  const [selectedSectionId, setSelectedSectionId] = useState<number | undefined>(undefined);
  const [selectedLessonId, setSelectedLessonId] = useState<number | undefined>(undefined);

  // Fetch course details to get lessons
  useEffect(() => {
    const fetchCourse = async () => {
      if (!courseId) return;

      try {
        setIsLoading(true);
        const courseData = await getFullCourseDetails(parseInt(courseId));
        setCourse(courseData);
      } catch (err: any) {
        console.error('Error fetching course:', err);
        toast.error('Failed to load course details');
      } finally {
        setIsLoading(false);
      }
    };

    fetchCourse();
  }, [courseId]);

  // Initialize selections based on URL parameters
  useEffect(() => {
    if (!course) return;

    if (lessonId) {
      // Lesson quiz
      setAssignmentType('lesson');
      setSelectedCourseId(parseInt(courseId!));
      setSelectedSectionId(parseInt(sectionId!));
      setSelectedLessonId(parseInt(lessonId));
    } else if (sectionId) {
      // Section quiz
      setAssignmentType('section');
      setSelectedCourseId(parseInt(courseId!));
      setSelectedSectionId(parseInt(sectionId));
    } else if (courseId) {
      // Course quiz
      setSelectedCourseId(parseInt(courseId));
      setAssignmentType('course');
    }
  }, [courseId, sectionId, lessonId, course]);

  // Reset selections when assignment type changes
  useEffect(() => {
    if (assignmentType === 'none') {
      setSelectedCourseId(undefined);
      setSelectedSectionId(undefined);
      setSelectedLessonId(undefined);
    } else if (assignmentType === 'course') {
      setSelectedSectionId(undefined);
      setSelectedLessonId(undefined);
    } else if (assignmentType === 'section') {
      setSelectedLessonId(undefined);
    }
  }, [assignmentType]);

  // Get all sections from course
  const getSections = (): Section[] => {
    if (!course || assignmentType !== 'section' && assignmentType !== 'lesson') return [];
    return course.sections;
  };

  // Get all lessons from selected section or all sections
  const getLessons = (): Lesson[] => {
    if (!course || assignmentType !== 'lesson') return [];

    if (selectedSectionId) {
      const section = course.sections.find((s) => s.id === selectedSectionId);
      return section?.lessons || [];
    }

    return course.sections.flatMap((section) => section.lessons);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!title.trim()) {
      toast.error('Please enter a quiz title');
      return;
    }

    if (passingScore < 0 || passingScore > 100) {
      toast.error('Passing score must be between 0 and 100');
      return;
    }

    try {
      setIsCreating(true);

      const data: CreateQuizRequest = {
        title: title.trim(),
        passingScore,
        courseId: selectedCourseId,
        sectionId: assignmentType === 'section' || assignmentType === 'lesson' ? selectedSectionId : undefined,
        lessonId: assignmentType === 'lesson' ? selectedLessonId : undefined,
      };

      const response = await createQuiz(data);
      toast.success('Quiz created successfully!');

      // Redirect to manage quiz page
      if (response.data?.id) {
        navigate(`/instructor/quiz/${response.data.id}/manage`);
      } else {
        navigate('/my-teaching');
      }
    } catch (err: any) {
      console.error('Error creating quiz:', err);
      toast.error(err.response?.data?.message || 'Failed to create quiz');
    } finally {
      setIsCreating(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
          <p className="text-gray-400 mt-4">Loading...</p>
        </div>
      </div>
    );
  }

  const sections = getSections();
  const lessons = getLessons();

  return (
    <div className="min-h-screen bg-gray-950 py-12">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-100 mb-2">Create New Quiz</h1>
          <p className="text-gray-400">
            Create a quiz {course ? `for "${course.title}"` : ''}
          </p>
        </div>

        <div className="bg-gray-800 border border-gray-700 rounded-xl p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Title */}
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-300 mb-2">
                Quiz Title <span className="text-red-400">*</span>
              </label>
              <input
                type="text"
                id="title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g., Quiz on Spring Boot Basics"
                maxLength={200}
                className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
                required
              />
              <p className="text-xs text-gray-400 mt-1">Maximum 200 characters</p>
            </div>

            {/* Passing Score */}
            <div>
              <label htmlFor="passingScore" className="block text-sm font-medium text-gray-300 mb-2">
                Passing Score (%) <span className="text-red-400">*</span>
              </label>
              <input
                type="number"
                id="passingScore"
                value={passingScore}
                onChange={(e) => setPassingScore(parseInt(e.target.value))}
                min={0}
                max={100}
                className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                required
              />
              <p className="text-xs text-gray-400 mt-1">
                Students must score at least this percentage to pass
              </p>
            </div>

            {/* Assignment Type Selection */}
            <div>
              <label htmlFor="assignmentType" className="block text-sm font-medium text-gray-300 mb-2">
                Assign Quiz To
              </label>
              <select
                id="assignmentType"
                value={assignmentType}
                onChange={(e) => setAssignmentType(e.target.value as AssignmentType)}
                className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                disabled={!!(courseId || sectionId || lessonId)}
              >
                <option value="none">-- Standalone Quiz --</option>
                <option value="course">Entire Course</option>
                <option value="section">Specific Section</option>
                <option value="lesson">Specific Lesson</option>
              </select>
              <p className="text-xs text-gray-400 mt-1">
                {(courseId || sectionId || lessonId)
                  ? 'Assignment type pre-selected from context'
                  : 'Choose where this quiz should be accessible'}
              </p>
            </div>

            {/* Course Selection - shown only if assignment type is course/section/lesson */}
            {(assignmentType === 'course' || assignmentType === 'section' || assignmentType === 'lesson') && (
              <div>
                <label htmlFor="courseId" className="block text-sm font-medium text-gray-300 mb-2">
                  Course <span className="text-red-400">*</span>
                </label>
                <select
                  id="courseId"
                  value={selectedCourseId || ''}
                  onChange={(e) => setSelectedCourseId(e.target.value ? parseInt(e.target.value) : undefined)}
                  className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  disabled={!!courseId}
                  required
                >
                  <option value="">-- Select Course --</option>
                  {course && <option value={course.id}>{course.title}</option>}
                </select>
                <p className="text-xs text-gray-400 mt-1">
                  {courseId ? 'Course pre-selected from URL' : 'Select the course for this quiz'}
                </p>
              </div>
            )}

            {/* Section Selection - shown only if assignment type is section/lesson */}
            {(assignmentType === 'section' || assignmentType === 'lesson') && (
              <div>
                <label htmlFor="sectionId" className="block text-sm font-medium text-gray-300 mb-2">
                  Section {assignmentType === 'section' && <span className="text-red-400">*</span>}
                </label>
                <select
                  id="sectionId"
                  value={selectedSectionId || ''}
                  onChange={(e) => setSelectedSectionId(e.target.value ? parseInt(e.target.value) : undefined)}
                  className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  required={assignmentType === 'section'}
                  disabled={!!(sectionId || lessonId)}
                >
                  <option value="">-- Select Section --</option>
                  {sections.map((section) => (
                    <option key={section.id} value={section.id}>
                      {section.title}
                    </option>
                  ))}
                </select>
                <p className="text-xs text-gray-400 mt-1">
                  {(sectionId || lessonId)
                    ? 'Section pre-selected from context'
                    : assignmentType === 'lesson'
                    ? 'Optionally filter lessons by section'
                    : 'Select the section for this quiz'}
                </p>
              </div>
            )}

            {/* Lesson Selection - shown only if assignment type is lesson */}
            {assignmentType === 'lesson' && (
              <div>
                <label htmlFor="lessonId" className="block text-sm font-medium text-gray-300 mb-2">
                  Lesson <span className="text-red-400">*</span>
                </label>
                <select
                  id="lessonId"
                  value={selectedLessonId || ''}
                  onChange={(e) => setSelectedLessonId(e.target.value ? parseInt(e.target.value) : undefined)}
                  className="w-full px-4 py-2 bg-gray-900 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  required
                  disabled={!!lessonId}
                >
                  <option value="">-- Select Lesson --</option>
                  {lessons.map((lesson) => (
                    <option key={lesson.id} value={lesson.id}>
                      {lesson.title}
                    </option>
                  ))}
                </select>
                <p className="text-xs text-gray-400 mt-1">
                  {lessonId ? 'Lesson pre-selected from context' : 'Select the lesson for this quiz'}
                </p>
              </div>
            )}

            {/* Info Box */}
            <div className="bg-blue-900/20 border border-blue-700/50 rounded-lg p-4">
              <div className="flex items-start gap-3">
                <div className="flex-shrink-0">
                  <svg
                    className="w-5 h-5 text-blue-400"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                      clipRule="evenodd"
                    />
                  </svg>
                </div>
                <div>
                  <h3 className="text-blue-400 font-semibold text-sm">Next Steps</h3>
                  <p className="text-gray-300 text-sm mt-1">
                    After creating the quiz, you'll be able to add questions and configure answers.
                  </p>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-3 justify-end pt-4">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate(-1)}
                disabled={isCreating}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                disabled={isCreating || !title.trim()}
                isLoading={isCreating}
              >
                {isCreating ? 'Creating...' : 'Create Quiz'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
