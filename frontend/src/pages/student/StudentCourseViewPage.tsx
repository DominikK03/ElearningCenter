import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Check, Circle, Play, BookOpen, Download, ArrowLeft, FileText } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../../contexts/AuthContext';
import { getFullCourseDetails } from '../../services/courseService';
import { getStudentEnrollments, markLessonAsCompleted, getCompletedLessons } from '../../services/enrollmentService';
import { getCourseQuizzes } from '../../services/quizService';
import Button from '../../components/ui/Button';
import Skeleton from '../../components/ui/Skeleton';
import type { FullCourseDetails, Enrollment, Lesson, Section, Quiz } from '../../types/api';

export default function StudentCourseViewPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [course, setCourse] = useState<FullCourseDetails | null>(null);
  const [enrollment, setEnrollment] = useState<Enrollment | null>(null);
  const [currentLesson, setCurrentLesson] = useState<Lesson | null>(null);
  const [currentSection, setCurrentSection] = useState<Section | null>(null);
  const [completedLessons, setCompletedLessons] = useState<Set<number>>(new Set());
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [markingComplete, setMarkingComplete] = useState(false);
  const [expandedSections, setExpandedSections] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (id && user) {
      fetchCourseAndEnrollment();
    }
  }, [id, user]);

  const fetchCourseAndEnrollment = async () => {
    if (!id || !user) return;

    try {
      setLoading(true);
      const [courseResponse, enrollmentsData, quizzesData] = await Promise.all([
        getFullCourseDetails(parseInt(id)),
        getStudentEnrollments(user.id),
        getCourseQuizzes(parseInt(id)),
      ]);

      setCourse(courseResponse);
      setQuizzes(quizzesData || []);

      const currentEnrollment = enrollmentsData?.find(
        (e) => e.courseId === parseInt(id) && (e.status === 'ACTIVE' || e.status === 'COMPLETED')
      );

      if (!currentEnrollment) {
        toast.error('You are not enrolled in this course');
        navigate('/my-courses');
        return;
      }

      setEnrollment(currentEnrollment);

      const completedLessonIds = await getCompletedLessons(currentEnrollment.id);
      setCompletedLessons(new Set(completedLessonIds));

      if (courseResponse.sections.length > 0) {
        const firstSection = courseResponse.sections[0];
        if (firstSection.lessons.length > 0) {
          setCurrentSection(firstSection);
          setCurrentLesson(firstSection.lessons[0]);
          setExpandedSections(new Set([firstSection.id]));
        }
      }
    } catch (err: any) {
      console.error('Error fetching course:', err);
      toast.error(err.response?.data?.message || 'Failed to load course');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectLesson = (section: Section, lesson: Lesson) => {
    setCurrentSection(section);
    setCurrentLesson(lesson);
  };

  const handleMarkAsCompleted = async () => {
    if (!enrollment || !currentSection || !currentLesson || !user || !id) return;

    try {
      setMarkingComplete(true);
      await markLessonAsCompleted(
        enrollment.id,
        currentSection.id,
        currentLesson.id
      );

      setCompletedLessons((prev) => new Set(prev).add(currentLesson.id));

      const enrollmentsData = await getStudentEnrollments(user.id);
      const updatedEnrollment = enrollmentsData.find(
        (e) => e.courseId === parseInt(id) && (e.status === 'ACTIVE' || e.status === 'COMPLETED')
      );

      if (updatedEnrollment) {
        setEnrollment(updatedEnrollment);
      }

      toast.success('Lesson marked as completed!');

      moveToNextLesson();
    } catch (err: any) {
      console.error('Error marking lesson as completed:', err);
      toast.error(err.response?.data?.message || 'Failed to mark lesson as completed');
    } finally {
      setMarkingComplete(false);
    }
  };

  const moveToNextLesson = () => {
    if (!course || !currentSection || !currentLesson) return;

    const currentSectionIndex = course.sections.findIndex((s) => s.id === currentSection.id);
    const currentLessonIndex = currentSection.lessons.findIndex((l) => l.id === currentLesson.id);

    if (currentLessonIndex < currentSection.lessons.length - 1) {
      setCurrentLesson(currentSection.lessons[currentLessonIndex + 1]);
    }
    else if (currentSectionIndex < course.sections.length - 1) {
      const nextSection = course.sections[currentSectionIndex + 1];
      if (nextSection.lessons.length > 0) {
        setCurrentSection(nextSection);
        setCurrentLesson(nextSection.lessons[0]);
        setExpandedSections((prev) => new Set(prev).add(nextSection.id));
      }
    } else {
      toast.success('Congratulations! You have completed all lessons!');
    }
  };

  const toggleSection = (sectionId: number) => {
    setExpandedSections((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(sectionId)) {
        newSet.delete(sectionId);
      } else {
        newSet.add(sectionId);
      }
      return newSet;
    });
  };

  const isLessonCompleted = (lessonId: number) => {
    return completedLessons.has(lessonId);
  };

  const isLessonCurrent = (lessonId: number) => {
    return currentLesson?.id === lessonId;
  };

  const getEmbedUrl = (videoUrl: string) => {
    if (videoUrl.includes('youtube.com/watch')) {
      const url = new URL(videoUrl);
      const videoId = url.searchParams.get('v');
      return `https://www.youtube.com/embed/${videoId}`;
    }
    if (videoUrl.includes('youtu.be/')) {
      const videoId = videoUrl.split('youtu.be/')[1].split('?')[0];
      return `https://www.youtube.com/embed/${videoId}`;
    }
    return videoUrl;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-950">
        <div className="h-screen flex">
          <div className="w-80 border-r border-gray-800">
            <Skeleton className="h-full" />
          </div>
          <div className="flex-1 p-8">
            <Skeleton className="h-12 w-3/4 mb-4" />
            <Skeleton className="h-96 w-full" />
          </div>
        </div>
      </div>
    );
  }

  if (!course || !enrollment) {
    return (
      <div className="min-h-screen bg-gray-950 flex items-center justify-center">
        <div className="text-center">
          <BookOpen className="w-16 h-16 text-gray-600 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-300 mb-2">Course not found</h3>
          <Button variant="primary" onClick={() => navigate('/my-courses')}>
            Back to My Courses
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-950">
      {/* Progress Bar */}
      <div className="bg-gray-900 border-b border-gray-800 px-6 py-4">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-2">
            <Button
              variant="ghost"
              onClick={() => navigate('/my-courses')}
              className="flex items-center gap-2"
            >
              <ArrowLeft className="w-4 h-4" />
              Back to My Courses
            </Button>
            <span className="text-sm text-gray-400">
              Overall Progress: {enrollment.progressPercentage}%
            </span>
          </div>
          <div className="w-full bg-gray-800 rounded-full h-2">
            <div
              className="bg-gradient-to-r from-purple-500 to-pink-500 h-2 rounded-full transition-all duration-300"
              style={{ width: `${enrollment.progressPercentage}%` }}
            />
          </div>
        </div>
      </div>

      <div className="flex h-[calc(100vh-120px)]">
        {/* Sidebar */}
        <div className="w-80 bg-gray-900 border-r border-gray-800 overflow-y-auto">
          <div className="p-6">
            <h2 className="text-xl font-bold text-gray-100 mb-4">{course.title}</h2>

            <div className="space-y-2">
              {course.sections.map((section) => (
                <div key={section.id}>
                  <button
                    onClick={() => toggleSection(section.id)}
                    className="w-full flex items-center justify-between p-3 bg-gray-800 hover:bg-gray-750 rounded-lg transition-colors"
                  >
                    <span className="text-sm font-medium text-gray-100">
                      {section.title}
                    </span>
                    <span className="text-gray-400">
                      {expandedSections.has(section.id) ? '▼' : '▶'}
                    </span>
                  </button>

                  {expandedSections.has(section.id) && (
                    <div className="ml-4 mt-2 space-y-1">
                      {section.lessons.map((lesson) => (
                        <button
                          key={lesson.id}
                          onClick={() => handleSelectLesson(section, lesson)}
                          className={`w-full flex items-center gap-3 p-3 rounded-lg transition-colors text-left ${
                            isLessonCurrent(lesson.id)
                              ? 'bg-purple-900/30 border border-purple-500'
                              : 'bg-gray-800 hover:bg-gray-750'
                          }`}
                        >
                          {isLessonCompleted(lesson.id) ? (
                            <Check className="w-5 h-5 text-green-400 flex-shrink-0" />
                          ) : isLessonCurrent(lesson.id) ? (
                            <Play className="w-5 h-5 text-purple-400 flex-shrink-0" />
                          ) : (
                            <Circle className="w-5 h-5 text-gray-600 flex-shrink-0" />
                          )}
                          <span className="text-sm text-gray-300 flex-1">{lesson.title}</span>
                        </button>
                      ))}

                      {/* Section Quiz */}
                      {quizzes.filter(q => q.sectionId === section.id && !q.lessonId).map((quiz) => (
                        <button
                          key={quiz.id}
                          onClick={() => navigate(`/quiz/${quiz.id}/take`)}
                          className="w-full flex items-center gap-3 p-3 bg-purple-900/20 border border-purple-700/50 hover:bg-purple-900/30 rounded-lg transition-colors mt-2"
                        >
                          <FileText className="w-4 h-4 text-purple-400 flex-shrink-0" />
                          <div className="flex-1 text-left">
                            <div className="text-xs font-medium text-purple-300">{quiz.title}</div>
                            <div className="text-xs text-gray-500">Section Quiz • {quiz.questionsCount} questions</div>
                          </div>
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Course Quiz */}
            {quizzes.filter(q => q.courseId && !q.sectionId && !q.lessonId).map((quiz) => (
              <div key={quiz.id} className="mt-4 pt-4 border-t border-gray-800">
                <button
                  onClick={() => navigate(`/quiz/${quiz.id}/take`)}
                  className="w-full flex items-center gap-3 p-4 bg-purple-900/20 border border-purple-700/50 hover:bg-purple-900/30 rounded-lg transition-colors"
                >
                  <FileText className="w-5 h-5 text-purple-400 flex-shrink-0" />
                  <div className="flex-1 text-left">
                    <div className="text-sm font-medium text-purple-300">{quiz.title}</div>
                    <div className="text-xs text-gray-400 mt-1">Course Quiz • {quiz.questionsCount} questions</div>
                  </div>
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Main Content */}
        <div className="flex-1 overflow-y-auto">
          {currentLesson ? (
            <div className="max-w-4xl mx-auto p-8">
              <h1 className="text-3xl font-bold text-gray-100 mb-6">
                {currentLesson.title}
              </h1>

              {currentLesson.videoUrl && (
                <div className="mb-8">
                  <div className="bg-gray-900 rounded-xl overflow-hidden aspect-video">
                    <iframe
                      src={getEmbedUrl(currentLesson.videoUrl)}
                      title={currentLesson.title}
                      className="w-full h-full"
                      allowFullScreen
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    />
                  </div>
                </div>
              )}

              {currentLesson.content && (
                <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 mb-6">
                  <h3 className="text-lg font-semibold text-gray-100 mb-4">Lesson Content</h3>
                  <div className="prose prose-invert max-w-none">
                    <p className="text-gray-300 whitespace-pre-wrap">{currentLesson.content}</p>
                  </div>
                </div>
              )}

              {currentLesson.materials && currentLesson.materials.length > 0 && (
                <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 mb-6">
                  <h3 className="text-lg font-semibold text-gray-100 mb-4">Materials</h3>
                  <div className="space-y-2">
                    {currentLesson.materials.map((material) => (
                      <a
                        key={material.id}
                        href={material.fileUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-3 p-3 bg-gray-900 hover:bg-gray-750 rounded-lg transition-colors"
                      >
                        <Download className="w-5 h-5 text-purple-400" />
                        <div className="flex-1">
                          <p className="text-sm font-medium text-gray-100">{material.title}</p>
                          <p className="text-xs text-gray-500">{material.fileType}</p>
                        </div>
                      </a>
                    ))}
                  </div>
                </div>
              )}

              {/* Lesson Quiz */}
              {currentLesson && quizzes.filter(q => q.lessonId === currentLesson.id).map((quiz) => (
                <div key={quiz.id} className="bg-purple-900/20 border border-purple-700 rounded-xl p-6 mb-6">
                  <div className="flex items-start gap-4">
                    <div className="p-3 bg-purple-800/50 rounded-lg">
                      <FileText className="w-6 h-6 text-purple-300" />
                    </div>
                    <div className="flex-1">
                      <h3 className="text-lg font-semibold text-purple-100 mb-2">{quiz.title}</h3>
                      <p className="text-sm text-gray-400 mb-4">
                        Test your knowledge with {quiz.questionsCount} questions. Passing score: {quiz.passingScore}%
                      </p>
                      <Button
                        variant="primary"
                        onClick={() => navigate(`/quiz/${quiz.id}/take`)}
                        className="bg-purple-600 hover:bg-purple-700"
                      >
                        Take Quiz
                      </Button>
                    </div>
                  </div>
                </div>
              ))}

              <div className="flex gap-4">
                {!isLessonCompleted(currentLesson.id) && (
                  <Button
                    variant="primary"
                    onClick={handleMarkAsCompleted}
                    disabled={markingComplete}
                    className="flex-1"
                  >
                    {markingComplete ? 'Marking as Completed...' : 'Mark as Completed'}
                  </Button>
                )}
                {isLessonCompleted(currentLesson.id) && (
                  <div className="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-green-900/30 border border-green-700 rounded-lg">
                    <Check className="w-5 h-5 text-green-400" />
                    <span className="text-green-400 font-medium">Completed</span>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-center h-full">
              <div className="text-center">
                <BookOpen className="w-16 h-16 text-gray-600 mx-auto mb-4" />
                <p className="text-gray-400">Select a lesson to start learning</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
