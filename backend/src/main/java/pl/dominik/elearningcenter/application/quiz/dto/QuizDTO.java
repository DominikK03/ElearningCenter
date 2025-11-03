package pl.dominik.elearningcenter.application.quiz.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDTO(
        Long id,
        String title,
        int passingScore,
        Long courseId,
        Long sectionId,
        Long lessonId,
        Long instructorId,
        LocalDateTime createdAt,
        List<QuestionDTO> questions,
        int questionsCount,
        int maxScore
) {
}
