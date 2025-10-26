package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuizCommand;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.quiz.event.QuizDeletedEvent;

@Service
public class DeleteQuizCommandHandler {
    private final QuizRepository quizRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeleteQuizCommandHandler(
            QuizRepository quizRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.quizRepository = quizRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(DeleteQuizCommand command) {
        Quiz quiz = quizRepository.findByIdAndInstructorIdOrThrow(
                command.quizId(),
                command.instructorId()
        );

        Long quizId = quiz.getId();
        Long instructorId = quiz.getInstructorId();

        quizRepository.delete(quiz);

        eventPublisher.publishEvent(new QuizDeletedEvent(quizId, instructorId));
    }
}
