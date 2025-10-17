package pl.dominik.elearningcenter.domain.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;

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

    default Course findByIdOrThrow(Long courseId){
        return findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + courseId));
    }
}
