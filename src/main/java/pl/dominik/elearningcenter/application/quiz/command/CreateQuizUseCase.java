package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.input.CreateQuizInput;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class CreateQuizUseCase {
    private final QuizRepository quizRepository;

    public CreateQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public Long execute(CreateQuizInput command) {
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
