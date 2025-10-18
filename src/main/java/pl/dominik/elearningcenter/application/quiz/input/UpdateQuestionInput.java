package pl.dominik.elearningcenter.application.quiz.input;

import java.util.List;

public record UpdateQuestionInput(
        Long quizId,
        Long questionId,
        String text,
        Integer orderIndex,
        int points,
        List<AddQuestionInput.AnswerInput> answers,
        Long instructorId
) {
}
