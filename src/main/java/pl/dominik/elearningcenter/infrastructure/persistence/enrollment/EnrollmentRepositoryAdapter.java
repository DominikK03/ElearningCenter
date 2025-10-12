package pl.dominik.elearningcenter.infrastructure.persistence.enrollment;

import org.springframework.stereotype.Repository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class EnrollmentRepositoryAdapter implements EnrollmentRepository {
    private final EnrollmentJpaRepository jpaRepository;

    public EnrollmentRepositoryAdapter(EnrollmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        return jpaRepository.save(enrollment);
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Enrollment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        return jpaRepository.findByStudentId(studentId);
    }

    @Override
    public List<Enrollment> findByCourseId(Long courseId) {
        return jpaRepository.findByCourseId(courseId);
    }

    @Override
    public Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return jpaRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public boolean existsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return jpaRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public void delete(Enrollment enrollment) {
        jpaRepository.delete(enrollment);
    }
}
