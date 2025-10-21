package pl.dominik.elearningcenter.infrastructure.persistence.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepositoryAdapter implements CourseRepository {

    private final CourseJpaRepository jpaRepository;

    private CourseRepositoryAdapter(CourseJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Course save(Course course) {
        return jpaRepository.save(course);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Course> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }


    @Override
    public Page<Course> findByInstructorId(Long instructorId, Pageable pageable) {
        return jpaRepository.findByInstructorId(instructorId, pageable);
    }

    @Override
    public List<Course> findByCategory(String category) {
        return jpaRepository.findByCategory(category);
    }

    @Override
    public Page<Course> findByPublished(boolean published, Pageable pageable) {
        return jpaRepository.findByPublished(published, pageable);
    }

    @Override
    public void delete(Course course) {
        jpaRepository.delete(course);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<Course> findByIdAndInstructorId(Long id, Long instructorId) {
        return jpaRepository.findByIdAndInstructorId(id, instructorId);
    }
}
