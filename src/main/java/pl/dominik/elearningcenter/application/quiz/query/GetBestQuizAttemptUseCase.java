package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.input.GetBestQuizAttemptInput;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;

import java.util.Optional;

@Service
public class GetBestQuizAttemptUseCase {
    private final QuizAttemptRepository attemptRepository;

    public GetBestQuizAttemptUseCase(QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Transactional(readOnly = true)
    public QuizAttemptDTO execute(GetBestQuizAttemptInput command) {
        Optional<QuizAttempt> bestAttempt = attemptRepository.findBestAttempt(
                command.quizId(),
                command.studentId()
        );

        return bestAttempt
                .map(QuizAttemptDTO::from)
                .orElse(null);
    }
}
