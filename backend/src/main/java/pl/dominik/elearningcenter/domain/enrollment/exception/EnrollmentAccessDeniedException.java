package pl.dominik.elearningcenter.domain.enrollment.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class EnrollmentAccessDeniedException extends DomainException {
    public EnrollmentAccessDeniedException(String message) {
        super(message);
    }
}
