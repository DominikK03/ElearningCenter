package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.input.GetQuizForStudentInput;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class GetQuizForStudentUseCase {
    private final QuizRepository quizRepository;

    public GetQuizForStudentUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public QuizDTO execute(GetQuizForStudentInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        return QuizDTO.forStudent(quiz);
    }
}
