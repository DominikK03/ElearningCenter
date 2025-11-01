package pl.dominik.elearningcenter.application.enrollment.query;

public record GetCompletedLessonsQuery(
        Long enrollmentId,
        Long userId
) {
}
