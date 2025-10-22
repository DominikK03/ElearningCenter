package pl.dominik.elearningcenter.application.enrollment.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentStatus;
import pl.dominik.elearningcenter.domain.enrollment.exception.EnrollmentNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnenrollStudentCommandHandlerTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private UnenrollStudentCommandHandler handler;

    @Test
    void shouldUnenrollStudentSuccessfully() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);

        handler.handle(new UnenrollStudentCommand(1L, 5L));

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void shouldThrowExceptionWhenEnrollmentNotFound() {
        when(enrollmentRepository.findByIdOrThrow(999L))
                .thenThrow(new EnrollmentNotFoundException("Enrollment not found"));

        assertThatThrownBy(() -> handler.handle(new UnenrollStudentCommand(999L, 5L)))
                .isInstanceOf(EnrollmentNotFoundException.class);

        verify(enrollmentRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenStudentDoesNotOwnEnrollment() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);

        assertThatThrownBy(() -> handler.handle(new UnenrollStudentCommand(1L, 999L)))
                .isInstanceOf(DomainException.class);

        verify(enrollmentRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenTryingToUnenrollCompletedEnrollment() {
        Enrollment enrollment = Enrollment.enroll(5L, 10L);
        enrollment.complete();
        when(enrollmentRepository.findByIdOrThrow(1L)).thenReturn(enrollment);

        assertThatThrownBy(() -> handler.handle(new UnenrollStudentCommand(1L, 5L)))
                .isInstanceOf(IllegalStateException.class);

        verify(enrollmentRepository, never()).delete(any());
    }
}
