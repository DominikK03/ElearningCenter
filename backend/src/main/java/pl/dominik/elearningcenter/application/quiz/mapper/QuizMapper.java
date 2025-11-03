package pl.dominik.elearningcenter.application.quiz.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.domain.quiz.Quiz;

@Component
public class QuizMapper {

    private final QuestionMapper questionMapper;

    public QuizMapper(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public QuizDTO toDto(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPassingScore(),
                quiz.getCourseId(),
                quiz.getSectionId(),
                quiz.getLessonId(),
                quiz.getInstructorId(),
                quiz.getCreatedAt(),
                quiz.getQuestions().stream()
                        .map(questionMapper::toDto)
                        .toList(),
                quiz.getQuestionsCount(),
                quiz.calculateMaxScore()
        );
    }

    public QuizDTO toDtoForStudent(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPassingScore(),
                quiz.getCourseId(),
                quiz.getSectionId(),
                quiz.getLessonId(),
                quiz.getInstructorId(),
                quiz.getCreatedAt(),
                quiz.getQuestions().stream()
                        .map(questionMapper::toDtoForStudent)
                        .toList(),
                quiz.getQuestionsCount(),
                quiz.calculateMaxScore()
        );
    }

    public QuizDTO toLightDto(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPassingScore(),
                quiz.getCourseId(),
                quiz.getSectionId(),
                quiz.getLessonId(),
                quiz.getInstructorId(),
                quiz.getCreatedAt(),
                java.util.Collections.emptyList(),
                quiz.getQuestionsCount(),
                quiz.calculateMaxScore()
        );
    }
}
