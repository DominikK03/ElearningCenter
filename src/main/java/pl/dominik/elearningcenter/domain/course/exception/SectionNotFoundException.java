package pl.dominik.elearningcenter.domain.course.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class SectionNotFoundException extends DomainException {
    public SectionNotFoundException(String message) {
        super(message);
    }
}
