package pl.dominik.elearningcenter.application.quiz.dto;

import pl.dominik.elearningcenter.domain.quiz.Quiz;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDTO(
        Long id,
        String title,
        int passingScore,
        Long lessonId,
        Long instructorId,
        LocalDateTime createdAt,
        List<QuestionDTO> questions,
        int questionsCount,
        int maxScore
) {
    public static QuizDTO from(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPassingScore(),
                quiz.getLessonId(),
                quiz.getInstructorId(),
                quiz.getCreatedAt(),
                quiz.getQuestions().stream()
                        .map(QuestionDTO::from)
                        .toList(),
                quiz.getQuestionsCount(),
                quiz.calculateMaxScore()
        );
    }

    public static QuizDTO forStudent(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPassingScore(),
                quiz.getLessonId(),
                quiz.getInstructorId(),
                quiz.getCreatedAt(),
                quiz.getQuestions().stream()
                        .map(QuestionDTO::forStudent)
                        .toList(),
                quiz.getQuestionsCount(),
                quiz.calculateMaxScore()
        );
    }
}
