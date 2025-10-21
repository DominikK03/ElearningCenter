package pl.dominik.elearningcenter.application.course.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.mapper.CourseMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCourseDetailsQueryHandlerTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private GetCourseDetailsQueryHandler handler;

    @Test
    void shouldReturnCourseDTOWhenCourseExists() {
        Course course = Course.create(
                "Java Fundamentals",
                "Learn Java from scratch",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        ReflectionTestUtils.setField(course, "id", 1L);

        Section section = new Section("Introduction", 0);
        ReflectionTestUtils.setField(section, "id", 10L);
        course.addSection(section);

        Lesson lesson = new Lesson("First Lesson", "Content here", 0);
        ReflectionTestUtils.setField(lesson, "id", 20L);
        section.addLesson(lesson);

        CourseDTO expectedDTO = new CourseDTO(
                1L,
                "Java Fundamentals",
                "Learn Java from scratch",
                java.math.BigDecimal.valueOf(99.99),
                "USD",
                null,
                "Programming",
                CourseLevel.BEGINNER,
                100L,
                false,
                null,
                null,
                1,
                1
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(expectedDTO);

        Optional<CourseDTO> result = handler.handle(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().title()).isEqualTo("Java Fundamentals");
        assertThat(result.get().description()).isEqualTo("Learn Java from scratch");
        assertThat(result.get().price()).isEqualTo(java.math.BigDecimal.valueOf(99.99));
        assertThat(result.get().level()).isEqualTo(CourseLevel.BEGINNER);
        assertThat(result.get().sectionsCount()).isEqualTo(1);
        assertThat(result.get().totalLessonsCount()).isEqualTo(1);

        verify(courseRepository, times(1)).findById(1L);
        verify(courseMapper, times(1)).toDto(course);
    }

    @Test
    void shouldReturnEmptyOptionalWhenCourseNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<CourseDTO> result = handler.handle(999L);

        assertThat(result).isEmpty();

        verify(courseRepository, times(1)).findById(999L);
        verify(courseMapper, never()).toDto(any());
    }

    @Test
    void shouldReturnCourseWithMultipleSectionsAndLessons() {
        Course course = Course.create(
                "Advanced Java",
                "Deep dive into Java",
                199.99,
                "USD",
                50L,
                "Programming",
                CourseLevel.ADVANCED
        );
        ReflectionTestUtils.setField(course, "id", 2L);

        Section section1 = new Section("Basics", 0);
        Section section2 = new Section("Advanced", 1);
        ReflectionTestUtils.setField(section1, "id", 11L);
        ReflectionTestUtils.setField(section2, "id", 12L);

        Lesson lesson1 = new Lesson("Lesson 1", "Content 1", 0);
        Lesson lesson2 = new Lesson("Lesson 2", "Content 2", 1);
        Lesson lesson3 = new Lesson("Lesson 3", "Content 3", 0);
        ReflectionTestUtils.setField(lesson1, "id", 21L);
        ReflectionTestUtils.setField(lesson2, "id", 22L);
        ReflectionTestUtils.setField(lesson3, "id", 23L);

        section1.addLesson(lesson1);
        section1.addLesson(lesson2);
        section2.addLesson(lesson3);

        course.addSection(section1);
        course.addSection(section2);

        CourseDTO expectedDTO = new CourseDTO(
                2L,
                "Advanced Java",
                "Deep dive into Java",
                java.math.BigDecimal.valueOf(199.99),
                "USD",
                null,
                "Programming",
                CourseLevel.ADVANCED,
                50L,
                false,
                null,
                null,
                2,
                3
        );

        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(expectedDTO);

        Optional<CourseDTO> result = handler.handle(2L);

        assertThat(result).isPresent();
        assertThat(result.get().sectionsCount()).isEqualTo(2);
        assertThat(result.get().totalLessonsCount()).isEqualTo(3);

        verify(courseRepository, times(1)).findById(2L);
        verify(courseMapper, times(1)).toDto(course);
    }

    @Test
    void shouldReturnCourseWithNoSections() {
        Course course = Course.create(
                "Empty Course",
                "No content yet",
                0.0,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        ReflectionTestUtils.setField(course, "id", 3L);

        CourseDTO expectedDTO = new CourseDTO(
                3L,
                "Empty Course",
                "No content yet",
                java.math.BigDecimal.ZERO,
                "USD",
                null,
                "Programming",
                CourseLevel.BEGINNER,
                100L,
                false,
                null,
                null,
                0,
                0
        );

        when(courseRepository.findById(3L)).thenReturn(Optional.of(course));
        when(courseMapper.toDto(course)).thenReturn(expectedDTO);

        Optional<CourseDTO> result = handler.handle(3L);

        assertThat(result).isPresent();
        assertThat(result.get().sectionsCount()).isEqualTo(0);
        assertThat(result.get().totalLessonsCount()).isEqualTo(0);

        verify(courseRepository, times(1)).findById(3L);
        verify(courseMapper, times(1)).toDto(course);
    }
}
