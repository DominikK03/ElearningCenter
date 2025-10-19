package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuestionCommand;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class DeleteQuestionCommandHandler {
    private final QuizRepository quizRepository;

    public DeleteQuestionCommandHandler(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void handle(DeleteQuestionCommand command) {
        Quiz quiz = quizRepository.findByIdAndInstructorIdOrThrow(
                command.quizId(),
                command.instructorId()
        );
        quiz.removeQuestion(command.questionId());
    }
}
