package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizAttemptMapper;
import pl.dominik.elearningcenter.application.quiz.query.GetBestQuizAttemptQuery;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;

import java.util.Optional;

@Service
public class GetBestQuizAttemptQueryHandler {
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptMapper quizAttemptMapper;

    public GetBestQuizAttemptQueryHandler(
            QuizAttemptRepository attemptRepository,
            QuizAttemptMapper quizAttemptMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    @Transactional(readOnly = true)
    public Optional<QuizAttemptDTO> handle(GetBestQuizAttemptQuery command) {
        return attemptRepository.findBestAttempt(
                command.quizId(),
                command.studentId()
        )
                .map(quizAttemptMapper::toDto);
    }
}
