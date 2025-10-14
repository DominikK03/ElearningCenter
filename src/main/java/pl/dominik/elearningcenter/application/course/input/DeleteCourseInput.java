package pl.dominik.elearningcenter.application.course.input;

public record DeleteCourseInput(
        Long courseId,
        Long instructorId
) {
}
