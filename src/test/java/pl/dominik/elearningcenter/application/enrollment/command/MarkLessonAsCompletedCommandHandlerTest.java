package pl.dominik.elearningcenter.application.enrollment.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLesson;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLessonRepository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentStatus;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkLessonAsCompletedCommandHandlerTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CompletedLessonRepository completedLessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private MarkLessonAsCompletedCommandHandler handler;

    @Test
    void shouldMarkLessonAsCompletedAndUpdateProgress() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        Course course = createCourseWithLessons(10L, 20L, 30L, 4);

        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);
        when(courseRepository.findByIdOrThrow(10L)).thenReturn(course);
        when(completedLessonRepository.existsByEnrollmentIdAndLessonId(1L, 30L)).thenReturn(false);
        when(completedLessonRepository.countByEnrollmentId(1L)).thenReturn(1L);

        handler.handle(new MarkLessonAsCompletedCommand(1L, 20L, 30L, 5L));

        assertThat(enrollment.getProgress().getPercentage()).isEqualTo(25);
    }

    @Test
    void shouldCompleteEnrollmentWhenAllLessonsCompleted() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        Course course = createCourseWithLessons(10L, 20L, 30L, 2);

        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);
        when(courseRepository.findByIdOrThrow(10L)).thenReturn(course);
        when(completedLessonRepository.existsByEnrollmentIdAndLessonId(1L, 30L)).thenReturn(false);
        when(completedLessonRepository.countByEnrollmentId(1L)).thenReturn(2L);

        handler.handle(new MarkLessonAsCompletedCommand(1L, 20L, 30L, 5L));

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    void shouldNotSaveDuplicateCompletedLesson() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        Course course = createCourseWithLessons(10L, 20L, 30L, 4);

        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);
        when(courseRepository.findByIdOrThrow(10L)).thenReturn(course);
        when(completedLessonRepository.existsByEnrollmentIdAndLessonId(1L, 30L)).thenReturn(true);

        handler.handle(new MarkLessonAsCompletedCommand(1L, 20L, 30L, 5L));

        verify(completedLessonRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStudentDoesNotOwnEnrollment() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);

        assertThatThrownBy(() -> handler.handle(new MarkLessonAsCompletedCommand(1L, 20L, 30L, 999L)))
                .isInstanceOf(DomainException.class);

        verify(completedLessonRepository, never()).save(any());
    }

    private Course createCourseWithLessons(Long courseId, Long sectionId, Long lessonId, int totalLessons) {
        Course course = Course.create(
                "Test Course",
                "Description",
                99.99,
                "USD",
                1L,
                "Programming",
                CourseLevel.BEGINNER
        );

        Section section = new Section("Test Section", 1);
        ReflectionTestUtils.setField(section, "id", sectionId);

        for (int i = 0; i < totalLessons; i++) {
            Lesson lesson = new Lesson("Lesson " + i, "Content " + i, i + 1);
            if (i == 0) {
                ReflectionTestUtils.setField(lesson, "id", lessonId);
            }
            section.addLesson(lesson);
        }
        course.addSection(section);

        return course;
    }
}
