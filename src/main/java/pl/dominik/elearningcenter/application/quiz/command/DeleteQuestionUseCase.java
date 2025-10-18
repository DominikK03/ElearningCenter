package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.input.DeleteQuestionInput;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class DeleteQuestionUseCase {
    private final QuizRepository quizRepository;

    public DeleteQuestionUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void execute(DeleteQuestionInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        quiz.ensureOwnedBy(command.instructorId());
        quiz.removeQuestion(command.questionId());
    }
}
