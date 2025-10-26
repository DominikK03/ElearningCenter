package pl.dominik.elearningcenter.application.quiz.query;

public record GetQuizForStudentQuery(
        Long quizId,
        Long studentId
) {
}
