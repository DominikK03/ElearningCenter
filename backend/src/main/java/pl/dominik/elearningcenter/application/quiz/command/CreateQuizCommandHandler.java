package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.CreateQuizCommand;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class CreateQuizCommandHandler {
    private final QuizRepository quizRepository;

    public CreateQuizCommandHandler(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public Long handle(CreateQuizCommand command) {
        Quiz quiz = Quiz.create(
                command.title(),
                command.passingScore(),
                command.instructorId(),
                command.lessonId()
        );
        quizRepository.save(quiz);
        return quiz.getId();
    }
}
