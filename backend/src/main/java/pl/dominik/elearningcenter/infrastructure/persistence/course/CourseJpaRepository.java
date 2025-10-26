package pl.dominik.elearningcenter.infrastructure.persistence.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.dominik.elearningcenter.domain.course.Course;

import java.util.List;
import java.util.Optional;

interface CourseJpaRepository extends JpaRepository<Course, Long> {
    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);
    List<Course> findByCategory(String category);
    Page<Course> findByPublished(boolean published, Pageable pageable);
    Optional<Course> findByIdAndInstructorId(Long id, Long instructorId);
}
