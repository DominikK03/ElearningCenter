package pl.dominik.elearningcenter.domain.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    Course save(Course course);

    Optional<Course> findById(Long id);

    List<Course> findAll();
    Page<Course> findAll(Pageable pageable);

    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    List<Course> findByCategory(String category);

    Page<Course> findByPublished(boolean published, Pageable pageable);

    void delete(Course course);

    boolean existsById(Long id);

    Optional<Course> findByIdAndInstructorId(Long id, Long instructorId);

    default Course findByIdOrThrow(Long courseId){
        return findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + courseId));
    }

    default Course findByIdAndInstructorIdOrThrow(Long courseId, Long instructorId) {
        return findByIdAndInstructorId(courseId, instructorId)
                .orElseThrow(() -> {
                    if (!existsById(courseId)) {
                        return new CourseNotFoundException("Course not found: " + courseId);
                    }
                    return new DomainException("Only course owner can perform this action");
                });
    }
}
