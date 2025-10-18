package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.input.GetQuizDetailsInput;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class GetQuizDetailsUseCase {
    private final QuizRepository quizRepository;

    public GetQuizDetailsUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public QuizDTO execute(GetQuizDetailsInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        if (!quiz.isOwnedBy(command.instructorId())) {
            throw new DomainException("Permission denied. You're not owner of this quiz");
        }
        return QuizDTO.from(quiz);
    }
}
