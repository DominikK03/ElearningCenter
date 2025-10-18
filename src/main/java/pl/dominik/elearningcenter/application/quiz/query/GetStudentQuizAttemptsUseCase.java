package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.input.GetStudentQuizAttemptsInput;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;

import java.util.List;

@Service
public class GetStudentQuizAttemptsUseCase {
    private final QuizAttemptRepository attemptRepository;

    public GetStudentQuizAttemptsUseCase(QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptDTO> execute(GetStudentQuizAttemptsInput command) {
        List<QuizAttempt> attempts = attemptRepository.findByQuizIdAndStudentId(
                command.quizId(),
                command.studentId()
        );

        return attempts.stream()
                .map(QuizAttemptDTO::from)
                .toList();
    }
}
