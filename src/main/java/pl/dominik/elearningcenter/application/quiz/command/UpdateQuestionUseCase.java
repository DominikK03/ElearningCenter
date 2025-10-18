package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.input.UpdateQuestionInput;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.List;

@Service
public class UpdateQuestionUseCase {
    private final QuizRepository quizRepository;

    public UpdateQuestionUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void execute(UpdateQuestionInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        quiz.ensureOwnedBy(command.instructorId());
        Question question = quiz.findQuestion(command.questionId());
        question.updateText(command.text());
        question.updateOrderIndex(command.orderIndex());
        question.updatePoints(command.points());

        List<Answer> answers = command.answers().stream()
                .map(answerInput -> Answer.of(
                        answerInput.text(),
                        answerInput.correct()
                ))
                .toList();

        question.setAnswers(answers);
    }
}
