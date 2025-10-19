package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizMapper;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizDetailsQuery;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class GetQuizDetailsQueryHandler {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    public GetQuizDetailsQueryHandler(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    @Transactional(readOnly = true)
    public QuizDTO handle(GetQuizDetailsQuery command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        if (!quiz.isOwnedBy(command.instructorId())) {
            throw new DomainException("Permission denied. You're not owner of this quiz");
        }
        return quizMapper.toDto(quiz);
    }
}
