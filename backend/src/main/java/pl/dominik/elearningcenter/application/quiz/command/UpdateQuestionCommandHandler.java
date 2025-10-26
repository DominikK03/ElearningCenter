package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuestionCommand;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.List;

@Service
public class UpdateQuestionCommandHandler {
    private final QuizRepository quizRepository;

    public UpdateQuestionCommandHandler(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public void handle(UpdateQuestionCommand command) {
        Quiz quiz = quizRepository.findByIdAndInstructorIdOrThrow(
                command.quizId(),
                command.instructorId()
        );
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
