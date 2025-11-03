package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.AddQuestionCommand;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.List;

@Service
public class AddQuestionCommandHandler {
    private final QuizRepository quizRepository;

    public AddQuestionCommandHandler(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public Long handle(AddQuestionCommand command) {
        Quiz quiz = quizRepository.findByIdAndInstructorIdOrThrow(
                command.quizId(),
                command.instructorId()
        );
        Question newQuestion = new Question(
                command.text(),
                command.type(),
                command.orderIndex()
        );

        newQuestion.updatePoints(command.points());

        List<Answer> answers = command.answers().stream()
                .map(answerInput -> Answer.of(answerInput.text(), answerInput.correct()))
                .toList();
        newQuestion.setAnswers(answers);

        quiz.addQuestion(newQuestion);
        return newQuestion.getId();
    }
}
