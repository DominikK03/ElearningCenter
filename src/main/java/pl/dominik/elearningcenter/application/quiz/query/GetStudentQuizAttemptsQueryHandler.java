package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizAttemptMapper;
import pl.dominik.elearningcenter.application.quiz.query.GetStudentQuizAttemptsQuery;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;

import java.util.List;

@Service
public class GetStudentQuizAttemptsQueryHandler {
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptMapper quizAttemptMapper;

    public GetStudentQuizAttemptsQueryHandler(
            QuizAttemptRepository attemptRepository,
            QuizAttemptMapper quizAttemptMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptDTO> handle(GetStudentQuizAttemptsQuery command) {
        List<QuizAttempt> attempts = attemptRepository.findByQuizIdAndStudentId(
                command.quizId(),
                command.studentId()
        );

        return attempts.stream()
                .map(quizAttemptMapper::toDto)
                .toList();
    }
}
