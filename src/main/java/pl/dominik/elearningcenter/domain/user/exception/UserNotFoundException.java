package pl.dominik.elearningcenter.domain.user.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
