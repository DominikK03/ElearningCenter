package pl.dominik.elearningcenter.application.enrollment.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.mapper.EnrollmentMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotPublishedException;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentStatus;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollStudentCommandHandlerTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private EnrollStudentCommandHandler handler;

    @Test
    void shouldEnrollStudentSuccessfully() {
        Course course = createPublishedCourse();

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 10L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));
        when(enrollmentMapper.toDto(any(Enrollment.class))).thenReturn(
                new EnrollmentDTO(1L, 1L, 10L, 0, null, null, EnrollmentStatus.ACTIVE)
        );

        EnrollmentDTO result = handler.handle(new EnrollStudentCommand(1L, 10L));

        assertThat(result.studentId()).isEqualTo(1L);
        assertThat(result.courseId()).isEqualTo(10L);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new EnrollStudentCommand(1L, 999L)))
                .isInstanceOf(CourseNotFoundException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCourseIsNotPublished() {
        Course course = createUnpublishedCourse();

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> handler.handle(new EnrollStudentCommand(1L, 10L)))
                .isInstanceOf(CourseNotPublishedException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStudentAlreadyEnrolled() {
        Course course = createPublishedCourse();

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(new EnrollStudentCommand(1L, 10L)))
                .isInstanceOf(DomainException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    private Course createPublishedCourse() {
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
        ReflectionTestUtils.setField(section, "id", 1L);
        Lesson lesson = new Lesson("Test Lesson", "Content", 1);
        section.addLesson(lesson);
        course.addSection(section);

        course.publish();
        return course;
    }

    private Course createUnpublishedCourse() {
        return Course.create(
                "Test Course",
                "Description",
                99.99,
                "USD",
                1L,
                "Programming",
                CourseLevel.BEGINNER
        );
    }
}
