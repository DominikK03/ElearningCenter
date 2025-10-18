package pl.dominik.elearningcenter.application.quiz.input;

import pl.dominik.elearningcenter.domain.quiz.QuestionType;

import java.util.List;

public record AddQuestionInput(
        Long quizId,
        String text,
        QuestionType type,
        Integer orderIndex,
        List<AnswerInput> answers,
        Long instructorId
) {
    public record AnswerInput(
            String text,
            boolean correct
    ) {}
}


