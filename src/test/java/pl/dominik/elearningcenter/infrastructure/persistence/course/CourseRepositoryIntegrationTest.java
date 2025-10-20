package pl.dominik.elearningcenter.infrastructure.persistence.course;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(CourseRepositoryAdapter.class)
class CourseRepositoryIntegrationTest {

    @Autowired
    private CourseRepositoryAdapter courseRepository;

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    @Test
    void shouldSaveAndRetrieveCourse() {
        Course course = Course.create(
                "Integration Test Course",
                "Testing course persistence",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );

        courseRepository.save(course);
        Optional<Course> retrieved = courseRepository.findById(course.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle().getValue()).isEqualTo("Integration Test Course");
        assertThat(retrieved.get().getDescription().getValue()).isEqualTo("Testing course persistence");
        assertThat(retrieved.get().getPrice().getAmount()).isEqualTo(java.math.BigDecimal.valueOf(99.99));
        assertThat(retrieved.get().getPrice().getCurrencyCode()).isEqualTo("USD");
        assertThat(retrieved.get().getInstructorId()).isEqualTo(100L);
        assertThat(retrieved.get().getCategory()).isEqualTo("Programming");
        assertThat(retrieved.get().getLevel()).isEqualTo(CourseLevel.BEGINNER);
        assertThat(retrieved.get().isPublished()).isFalse();
    }

    @Test
    void shouldFindCourseByIdAndInstructorId() {
        Course course = Course.create(
                "Authorized Course",
                "Only owner can access",
                149.99,
                "EUR",
                200L,
                "Security",
                CourseLevel.INTERMEDIATE
        );
        courseRepository.save(course);

        Optional<Course> retrieved = courseRepository.findByIdAndInstructorId(
                course.getId(),
                200L
        );

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getInstructorId()).isEqualTo(200L);
        assertThat(retrieved.get().getTitle().getValue()).isEqualTo("Authorized Course");
    }

    @Test
    void shouldReturnEmptyWhenCourseExistsButInstructorIdDoesNotMatch() {
        Course course = Course.create(
                "Restricted Course",
                "Different owner",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        courseRepository.save(course);

        Optional<Course> retrieved = courseRepository.findByIdAndInstructorId(
                course.getId(),
                999L
        );

        assertThat(retrieved).isEmpty();
    }

    @Test
    void shouldThrowExceptionWithFindByIdAndInstructorIdOrThrowWhenCourseNotFound() {
        assertThatThrownBy(() -> courseRepository.findByIdAndInstructorIdOrThrow(999L, 100L))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("Course not found: 999");
    }

    @Test
    void shouldThrowExceptionWithFindByIdAndInstructorIdOrThrowWhenInstructorIdDoesNotMatch() {
        Course course = Course.create(
                "Owner Protected Course",
                "Only owner can access",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        courseRepository.save(course);

        assertThatThrownBy(() -> courseRepository.findByIdAndInstructorIdOrThrow(course.getId(), 999L))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Only course owner can perform this action");
    }

    @Test
    void shouldSuccessfullyRetrieveCourseWithFindByIdAndInstructorIdOrThrow() {
        Course course = Course.create(
                "Authorized Access Course",
                "Correct owner",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        courseRepository.save(course);

        Course retrieved = courseRepository.findByIdAndInstructorIdOrThrow(course.getId(), 100L);

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(course.getId());
        assertThat(retrieved.getInstructorId()).isEqualTo(100L);
        assertThat(retrieved.getTitle().getValue()).isEqualTo("Authorized Access Course");
    }

    @Test
    void shouldSaveCourseWithSectionsAndLessons() {
        Course course = Course.create(
                "Complete Course",
                "With sections and lessons",
                199.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.ADVANCED
        );

        Section section1 = new Section("Introduction", 0);
        Section section2 = new Section("Advanced Topics", 1);

        Lesson lesson1 = new Lesson("Getting Started", "Introduction content", 0);
        Lesson lesson2 = new Lesson("Deep Dive", "Advanced content", 0);

        section1.addLesson(lesson1);
        section2.addLesson(lesson2);

        course.addSection(section1);
        course.addSection(section2);

        courseRepository.save(course);
        Course retrieved = courseRepository.findByIdOrThrow(course.getId());

        assertThat(retrieved.getSections()).hasSize(2);
        assertThat(retrieved.getSections().get(0).getTitle()).isEqualTo("Introduction");
        assertThat(retrieved.getSections().get(1).getTitle()).isEqualTo("Advanced Topics");
        assertThat(retrieved.getSections().get(0).getLessons()).hasSize(1);
        assertThat(retrieved.getSections().get(1).getLessons()).hasSize(1);
        assertThat(retrieved.getSections().get(0).getLessons().get(0).getTitle()).isEqualTo("Getting Started");
    }

    @Test
    void shouldPublishAndUnpublishCourse() {
        Course course = Course.create(
                "Publishable Course",
                "Can be published",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );

        Section section = new Section("Required Section", 0);
        Lesson lesson = new Lesson("Required Lesson", "Content", 0);
        section.addLesson(lesson);
        course.addSection(section);

        courseRepository.save(course);

        Course retrieved = courseRepository.findByIdOrThrow(course.getId());
        retrieved.publish();
        courseRepository.save(retrieved);

        Course published = courseRepository.findByIdOrThrow(course.getId());
        assertThat(published.isPublished()).isTrue();

        published.unpublish();
        courseRepository.save(published);

        Course unpublished = courseRepository.findByIdOrThrow(course.getId());
        assertThat(unpublished.isPublished()).isFalse();
    }

    @Test
    void shouldDeleteCourse() {
        Course course = Course.create(
                "Course to Delete",
                "Will be removed",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        courseRepository.save(course);
        Long courseId = course.getId();

        courseRepository.delete(course);

        Optional<Course> retrieved = courseRepository.findById(courseId);
        assertThat(retrieved).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenFindByIdOrThrowDoesNotFindCourse() {
        assertThatThrownBy(() -> courseRepository.findByIdOrThrow(999L))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining("Course not found: 999");
    }

    @Test
    void shouldUpdateCourseDetails() {
        Course course = Course.create(
                "Original Title",
                "Original Description",
                99.99,
                "USD",
                100L,
                "Programming",
                CourseLevel.BEGINNER
        );
        courseRepository.save(course);

        Course retrieved = courseRepository.findByIdOrThrow(course.getId());
        retrieved.updateTitle(new pl.dominik.elearningcenter.domain.course.valueobject.CourseTitle("Updated Title"));
        retrieved.updateDescription(new pl.dominik.elearningcenter.domain.course.valueobject.CourseDescription("Updated Description"));
        retrieved.updatePrice(pl.dominik.elearningcenter.domain.shared.valueobject.Money.of(java.math.BigDecimal.valueOf(149.99), "EUR"));
        retrieved.updateCategory("Advanced Programming");
        retrieved.updateLevel(CourseLevel.ADVANCED);
        courseRepository.save(retrieved);

        Course updated = courseRepository.findByIdOrThrow(course.getId());
        assertThat(updated.getTitle().getValue()).isEqualTo("Updated Title");
        assertThat(updated.getDescription().getValue()).isEqualTo("Updated Description");
        assertThat(updated.getPrice().getAmount()).isEqualTo(java.math.BigDecimal.valueOf(149.99));
        assertThat(updated.getPrice().getCurrencyCode()).isEqualTo("EUR");
        assertThat(updated.getCategory()).isEqualTo("Advanced Programming");
        assertThat(updated.getLevel()).isEqualTo(CourseLevel.ADVANCED);
    }
}
