package pl.dominik.elearningcenter.infrastructure.persistence.enrollment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentStatus;
import pl.dominik.elearningcenter.domain.enrollment.exception.EnrollmentNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(EnrollmentRepositoryAdapter.class)
class EnrollmentRepositoryIntegrationTest {

    @Autowired
    private EnrollmentRepositoryAdapter enrollmentRepository;

    @Autowired
    private EnrollmentJpaRepository enrollmentJpaRepository;

    @Test
    void shouldSaveAndRetrieveEnrollment() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> retrieved = enrollmentRepository.findById(enrollment.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStudentId()).isEqualTo(1L);
        assertThat(retrieved.get().getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    void shouldFindEnrollmentsByStudentId() {
        Enrollment enrollment1 = Enrollment.enroll(1L, 10L);
        Enrollment enrollment2 = Enrollment.enroll(1L, 20L);
        Enrollment enrollment3 = Enrollment.enroll(2L, 30L);

        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);
        enrollmentRepository.save(enrollment3);

        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(1L);

        assertThat(studentEnrollments).hasSize(2);
        assertThat(studentEnrollments).allMatch(e -> e.getStudentId().equals(1L));
    }

    @Test
    void shouldFindEnrollmentsByCourseId() {
        Enrollment enrollment1 = Enrollment.enroll(1L, 10L);
        Enrollment enrollment2 = Enrollment.enroll(2L, 10L);
        Enrollment enrollment3 = Enrollment.enroll(3L, 20L);

        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);
        enrollmentRepository.save(enrollment3);

        List<Enrollment> courseEnrollments = enrollmentRepository.findByCourseId(10L);

        assertThat(courseEnrollments).hasSize(2);
        assertThat(courseEnrollments).allMatch(e -> e.getCourseId().equals(10L));
    }


    @Test
    void shouldCheckIfEnrollmentExistsByStudentIdAndCourseId() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);
        enrollmentRepository.save(enrollment);

        assertThat(enrollmentRepository.existsByStudentIdAndCourseId(1L, 10L)).isTrue();
    }


    @Test
    void shouldCompleteEnrollmentAndPersistCompletedAt() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);
        enrollmentRepository.save(enrollment);

        enrollment.recalculateProgress(100);
        enrollmentRepository.save(enrollment);

        Optional<Enrollment> completed = enrollmentRepository.findById(enrollment.getId());
        assertThat(completed).isPresent();
        assertThat(completed.get().getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(completed.get().getCompletedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenEnrollmentNotFoundById() {
        assertThatThrownBy(() -> enrollmentRepository.findByIdOrThrow(999L))
                .isInstanceOf(EnrollmentNotFoundException.class);
    }
}
