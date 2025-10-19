package pl.dominik.elearningcenter.domain.quiz.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class QuestionNotFoundException extends DomainException {
    public QuestionNotFoundException(String message) {
        super(message);
    }
}
