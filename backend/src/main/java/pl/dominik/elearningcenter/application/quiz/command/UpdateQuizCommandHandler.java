package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuizCommand;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class UpdateQuizCommandHandler {
    private final QuizRepository quizRepository;

    public UpdateQuizCommandHandler(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void handle(UpdateQuizCommand command) {
        Quiz quiz = quizRepository.findByIdAndInstructorIdOrThrow(
                command.quizId(),
                command.instructorId()
        );
        quiz.updateTitle(command.title());
        quiz.updatePassingScore(command.passingScore());
    }
}
