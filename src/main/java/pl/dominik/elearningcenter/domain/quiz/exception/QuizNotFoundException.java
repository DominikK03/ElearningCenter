package pl.dominik.elearningcenter.domain.quiz.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class QuizNotFoundException extends DomainException {
    public QuizNotFoundException(String message) {
        super(message);
    }
}
