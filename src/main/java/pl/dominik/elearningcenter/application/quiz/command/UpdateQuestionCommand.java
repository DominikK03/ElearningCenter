package pl.dominik.elearningcenter.application.quiz.command;

import java.util.List;

public record UpdateQuestionCommand(
        Long quizId,
        Long questionId,
        String text,
        Integer orderIndex,
        int points,
        List<AddQuestionCommand.AnswerInput> answers,
        Long instructorId
) {
}
