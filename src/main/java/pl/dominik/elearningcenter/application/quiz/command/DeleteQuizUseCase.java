package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.input.DeleteQuizInput;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class DeleteQuizUseCase {
    private final QuizRepository quizRepository;

    public DeleteQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void execute(DeleteQuizInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        quiz.ensureOwnedBy(command.instructorId());
        quizRepository.delete(quiz);
    }
}
