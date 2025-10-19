package pl.dominik.elearningcenter.interfaces.rest.quiz.request;

import java.util.List;

public record SubmitQuizAttemptRequest(
        List<StudentAnswerRequest> answers
) {
    public record StudentAnswerRequest(
            Long questionId,
            List<Integer> selectedAnswerIndexes
    ) {}
}
