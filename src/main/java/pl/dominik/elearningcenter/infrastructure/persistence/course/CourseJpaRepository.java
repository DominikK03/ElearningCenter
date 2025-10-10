package pl.dominik.elearningcenter.infrastructure.persistence.course;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.dominik.elearningcenter.domain.course.Course;

import java.util.List;

interface CourseJpaRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);
    List<Course> findByCategory(String category);
    List<Course> findByPublished(boolean published);
}
