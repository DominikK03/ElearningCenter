package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.input.GetQuizAttemptResultInput;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class GetQuizAttemptResultUseCase {
    private final QuizAttemptRepository attemptRepository;

    public GetQuizAttemptResultUseCase(QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Transactional(readOnly = true)
    public QuizAttemptDTO execute(GetQuizAttemptResultInput command) {
        QuizAttempt attempt = attemptRepository.findByIdOrThrow(command.attemptId());
        if (!attempt.belongsToStudent(command.studentId())) {
            throw new DomainException("Permission denied. You cannot get other students attempt.");
        }
        return QuizAttemptDTO.from(attempt);
    }
}
