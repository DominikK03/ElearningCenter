package pl.dominik.elearningcenter.domain.course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {

    Course save(Course course);

    Optional<Course> findById(Long id);

    List<Course> findAll();

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByCategory(String category);

    List<Course> findByPublished(boolean published);

    void delete(Course course);

    boolean existsById(Long id);
}
