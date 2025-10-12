package pl.dominik.elearningcenter.application.course.command;

public record DeleteCourseCommand(
        Long courseId,
        Long instructorId
) {
}
