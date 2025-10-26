package pl.dominik.elearningcenter.application.course.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCourseCommandHandlerTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private UpdateCourseCommandHandler handler;

    @Test
    void shouldUpdateCourseWhenUserIsOwner() {
        Course course = Course.create(
                "Original Title",
                "Original Description",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        ReflectionTestUtils.setField(course, "id", 1L);

        UpdateCourseCommand command = new UpdateCourseCommand(
                1L,
                "Updated Title",
                "Updated Description",
                java.math.BigDecimal.valueOf(149.99),
                "EUR",
                "Advanced Programming",
                CourseLevel.ADVANCED,
                100L
        );

        when(courseRepository.findByIdAndInstructorIdOrThrow(1L, 100L))
                .thenReturn(course);

        handler.handle(command);

        assertThat(course.getTitle().getValue()).isEqualTo("Updated Title");
        assertThat(course.getDescription().getValue()).isEqualTo("Updated Description");
        assertThat(course.getPrice().getAmount()).isEqualTo(java.math.BigDecimal.valueOf(149.99));
        assertThat(course.getPrice().getCurrencyCode()).isEqualTo("EUR");
        assertThat(course.getCategory()).isEqualTo("Advanced Programming");
        assertThat(course.getLevel()).isEqualTo(CourseLevel.ADVANCED);

        verify(courseRepository, times(1)).findByIdAndInstructorIdOrThrow(1L, 100L);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        UpdateCourseCommand command = new UpdateCourseCommand(
                999L,
                "Title",
                "Description",
                java.math.BigDecimal.valueOf(99.99),
                "USD",
                "Category",
                CourseLevel.BEGINNER,
                100L
        );

        when(courseRepository.findByIdAndInstructorIdOrThrow(999L, 100L))
                .thenThrow(new CourseNotFoundException("Course not found: 999"));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("Course not found: 999");

        verify(courseRepository, times(1)).findByIdAndInstructorIdOrThrow(999L, 100L);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        UpdateCourseCommand command = new UpdateCourseCommand(
                1L,
                "Title",
                "Description",
                java.math.BigDecimal.valueOf(99.99),
                "USD",
                "Category",
                CourseLevel.BEGINNER,
                999L
        );

        when(courseRepository.findByIdAndInstructorIdOrThrow(1L, 999L))
                .thenThrow(new DomainException("Only course owner can perform this action"));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Only course owner can perform this action");

        verify(courseRepository, times(1)).findByIdAndInstructorIdOrThrow(1L, 999L);
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        Course course = Course.create(
                "Original Title",
                "Original Description",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        ReflectionTestUtils.setField(course, "id", 1L);

        UpdateCourseCommand command = new UpdateCourseCommand(
                1L,
                "Only Title Changed",
                "Original Description",
                java.math.BigDecimal.valueOf(99.99),
                "USD",
                "Programming",
                CourseLevel.BEGINNER,
                100L
        );

        when(courseRepository.findByIdAndInstructorIdOrThrow(1L, 100L))
                .thenReturn(course);

        handler.handle(command);

        assertThat(course.getTitle().getValue()).isEqualTo("Only Title Changed");
        assertThat(course.getDescription().getValue()).isEqualTo("Original Description");
        assertThat(course.getPrice().getAmount()).isEqualTo(java.math.BigDecimal.valueOf(99.99));
        assertThat(course.getLevel()).isEqualTo(CourseLevel.BEGINNER);
    }
}
