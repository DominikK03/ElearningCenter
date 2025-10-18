package pl.dominik.elearningcenter.application.quiz.input;

public record GetQuizForStudentInput(
        Long quizId,
        Long studentId
) {
}
