package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.input.AddQuestionInput;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class AddQuestionUseCase {
    private final QuizRepository quizRepository;

    public AddQuestionUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public Long execute(AddQuestionInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        quiz.ensureOwnedBy(command.instructorId());
        Question newQuestion = new Question(
                command.text(),
                command.type(),
                command.orderIndex()
        );
        quiz.addQuestion(newQuestion);
        return newQuestion.getId();
    }
}
