package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizMapper;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizForStudentQuery;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class GetQuizForStudentQueryHandler {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    public GetQuizForStudentQueryHandler(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    @Transactional(readOnly = true)
    public QuizDTO handle(GetQuizForStudentQuery command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        return quizMapper.toDtoForStudent(quiz);
    }
}
