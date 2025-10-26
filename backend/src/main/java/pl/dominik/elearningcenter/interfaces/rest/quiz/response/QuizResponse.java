package pl.dominik.elearningcenter.interfaces.rest.quiz.response;

import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;

import java.time.LocalDateTime;
import java.util.List;

public record QuizResponse(
        Long id,
        String title,
        int passingScore,
        Long lessonId,
        Long instructorId,
        LocalDateTime createdAt,
        List<QuestionResponse> questions,
        int questionsCount,
        int maxScore
) {
    public static QuizResponse from(QuizDTO dto) {
        return new QuizResponse(
                dto.id(),
                dto.title(),
                dto.passingScore(),
                dto.lessonId(),
                dto.instructorId(),
                dto.createdAt(),
                dto.questions().stream()
                        .map(QuestionResponse::from)
                        .toList(),
                dto.questionsCount(),
                dto.maxScore()
        );
    }
}
