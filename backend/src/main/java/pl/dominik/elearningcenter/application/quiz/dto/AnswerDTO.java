package pl.dominik.elearningcenter.application.quiz.dto;

public record AnswerDTO(
        String text,
        boolean correct,
        int index
) {
}
