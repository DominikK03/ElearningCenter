package pl.dominik.elearningcenter.domain.course.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class CourseNotPublishedException extends DomainException {
    public CourseNotPublishedException(String message) {
        super(message);
    }
}
