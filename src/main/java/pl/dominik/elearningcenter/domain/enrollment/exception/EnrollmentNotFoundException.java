package pl.dominik.elearningcenter.domain.enrollment.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class EnrollmentNotFoundException extends DomainException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
