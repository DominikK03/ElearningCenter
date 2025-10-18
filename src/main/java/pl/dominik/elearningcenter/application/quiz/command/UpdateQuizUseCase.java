package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.input.UpdateQuizInput;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class UpdateQuizUseCase {
    private final QuizRepository quizRepository;

    public UpdateQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void execute(UpdateQuizInput command) {
        Quiz quiz =  quizRepository.findByIdOrThrow(command.quizId());
        quiz.ensureOwnedBy(command.instructorId());
        quiz.updateTitle(command.title());
        quiz.updatePassingScore(command.passingScore());
    }
}
