package pl.dominik.elearningcenter.application.quiz.input;

import java.util.List;

public record SubmitQuizAttemptInput(
        Long quizId,
        Long studentId,
        List<StudentAnswerInput> answers
) {
    public record StudentAnswerInput(
            Long questionId,
            List<Integer> selectedAnswerIndexes
    ) {}
}
