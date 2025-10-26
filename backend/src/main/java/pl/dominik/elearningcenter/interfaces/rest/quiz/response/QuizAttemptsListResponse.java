package pl.dominik.elearningcenter.interfaces.rest.quiz.response;

import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;

import java.util.List;

public record QuizAttemptsListResponse(
        List<QuizAttemptResponse> attempts
) {
    public static QuizAttemptsListResponse from(List<QuizAttemptDTO> dtos) {
        return new QuizAttemptsListResponse(
                dtos.stream()
                        .map(QuizAttemptResponse::from)
                        .toList()
        );
    }
}
