package pl.dominik.elearningcenter.domain.quiz.exception;

import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

public class QuizAccessDeniedException extends DomainException {
    public QuizAccessDeniedException(String message) {
        super(message);
    }
}
