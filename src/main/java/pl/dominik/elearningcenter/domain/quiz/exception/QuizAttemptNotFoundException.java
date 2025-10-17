package pl.dominik.elearningcenter.domain.quiz.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class QuizAttemptNotFoundException extends DomainException {
    public QuizAttemptNotFoundException(String message) {
        super(message);
    }
}
