package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.AddQuestionCommand;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

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
        quiz.addQuestion(newQuestion);
        return newQuestion.getId();
    }
}
