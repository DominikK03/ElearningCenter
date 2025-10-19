package pl.dominik.elearningcenter.application.quiz.command;

import java.util.List;

public record SubmitQuizAttemptCommand(
        Long quizId,
        Long studentId,
        List<StudentAnswerInput> answers
) {
    public record StudentAnswerInput(
            Long questionId,
            List<Integer> selectedAnswerIndexes
    ) {}
}
