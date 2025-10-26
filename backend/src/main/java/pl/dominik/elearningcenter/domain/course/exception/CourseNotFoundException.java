package pl.dominik.elearningcenter.domain.course.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class CourseNotFoundException extends DomainException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}
