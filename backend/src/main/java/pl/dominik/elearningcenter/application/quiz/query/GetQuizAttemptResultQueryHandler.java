package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizAttemptMapper;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizAttemptResultQuery;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class GetQuizAttemptResultQueryHandler {
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptMapper quizAttemptMapper;

    public GetQuizAttemptResultQueryHandler(
            QuizAttemptRepository attemptRepository,
            QuizAttemptMapper quizAttemptMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    @Transactional(readOnly = true)
    public QuizAttemptDTO handle(GetQuizAttemptResultQuery command) {
        QuizAttempt attempt = attemptRepository.findByIdOrThrow(command.attemptId());
        if (!attempt.belongsToStudent(command.studentId())) {
            throw new DomainException("Permission denied. You cannot get other students attempt.");
        }
        return quizAttemptMapper.toDto(attempt);
    }
}
