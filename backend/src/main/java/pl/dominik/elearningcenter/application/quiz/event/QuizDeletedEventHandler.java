package pl.dominik.elearningcenter.application.quiz.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;
import pl.dominik.elearningcenter.domain.quiz.event.QuizDeletedEvent;

/**
 * Event handler that listens to QuizDeletedEvent and performs cleanup operations.
 * Deletes all quiz attempts associated with the deleted quiz.
 * Uses @TransactionalEventListener to ensure it runs before the transaction commits.
 */
@Component
public class QuizDeletedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(QuizDeletedEventHandler.class);

    private final QuizAttemptRepository attemptRepository;

    public QuizDeletedEventHandler(QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onQuizDeleted(QuizDeletedEvent event) {
        log.info("Handling QuizDeletedEvent for quizId={}", event.getQuizId());

        attemptRepository.deleteByQuizId(event.getQuizId());

        log.info("Successfully deleted all attempts for quizId={}", event.getQuizId());
    }
}
