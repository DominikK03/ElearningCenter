package pl.dominik.elearningcenter.domain.enrollment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnrollmentTest {

    @Test
    void shouldCreateEnrollmentSuccessfully() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);

        assertThat(enrollment.getStudentId()).isEqualTo(1L);
        assertThat(enrollment.getCourseId()).isEqualTo(10L);
        assertThat(enrollment.getProgress().getPercentage()).isEqualTo(0);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getEnrolledAt()).isNotNull();
    }

    @Test
    void shouldRecalculateProgressAndNotCompleteWhenProgressIsLessThan100() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);

        enrollment.recalculateProgress(50);

        assertThat(enrollment.getProgress().getPercentage()).isEqualTo(50);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    void shouldCompleteEnrollmentAutomaticallyWhenProgressReaches100() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);

        enrollment.recalculateProgress(100);

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(enrollment.getCompletedAt()).isNotNull();
    }

    @Test
    void shouldCompleteEnrollmentManually() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);

        enrollment.complete();

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    void shouldDropEnrollmentSuccessfully() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);

        enrollment.drop();

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
    }

    @Test
    void shouldThrowExceptionWhenDroppingCompletedEnrollment() {
        Enrollment enrollment = Enrollment.enroll(1L, 10L);
        enrollment.complete();

        assertThatThrownBy(() -> enrollment.drop())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot drop a completed enrollment");
    }

    @Test
    void shouldReturnTrueWhenEnrollmentBelongsToStudent() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);

        assertThat(enrollment.belongsToStudent(5L)).isTrue();
        assertThat(enrollment.belongsToStudent(999L)).isFalse();
    }
}
