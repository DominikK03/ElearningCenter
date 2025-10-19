package pl.dominik.elearningcenter.domain.quiz.event;

import pl.dominik.elearningcenter.domain.shared.event.DomainEvent;

import java.time.LocalDateTime;

/**
 * Domain event published when a Quiz is deleted.
 * This allows other parts of the system to react to quiz deletion.
 */
public class QuizDeletedEvent implements DomainEvent {

    private final Long quizId;
    private final Long instructorId;
    private final LocalDateTime occurredOn;

    public QuizDeletedEvent(Long quizId, Long instructorId) {
        this.quizId = quizId;
        this.instructorId = instructorId;
        this.occurredOn = LocalDateTime.now();
    }

    public Long getQuizId() {
        return quizId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String toString() {
        return "QuizDeletedEvent{" +
                "quizId=" + quizId +
                ", instructorId=" + instructorId +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
