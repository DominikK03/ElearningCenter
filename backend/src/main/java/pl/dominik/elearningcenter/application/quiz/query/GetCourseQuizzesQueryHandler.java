package pl.dominik.elearningcenter.application.quiz.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizMapper;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

import java.util.List;

@Service
public class GetCourseQuizzesQueryHandler {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    public GetCourseQuizzesQueryHandler(QuizRepository quizRepository, QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    public List<QuizDTO> handle(GetCourseQuizzesQuery query) {
        System.out.println("=== GetCourseQuizzesQueryHandler: courseId = " + query.courseId());
        List<Quiz> quizzes = quizRepository.findByCourseId(query.courseId());
        System.out.println("=== Found " + quizzes.size() + " quizzes");

        try {
            List<QuizDTO> dtos = quizzes.stream()
                    .map(quiz -> {
                        System.out.println("=== Mapping quiz: " + quiz.getId() + ", questions: " + quiz.getQuestions().size());
                        return quizMapper.toLightDto(quiz);
                    })
                    .toList();
            System.out.println("=== Successfully mapped " + dtos.size() + " DTOs");
            return dtos;
        } catch (Exception e) {
            System.err.println("=== ERROR mapping quizzes: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
