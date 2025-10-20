package pl.dominik.elearningcenter.application.course.command;

public record DeleteLessonCommand(
        Long courseId,
        Long sectionId,
        Long lessonId,
        Long instructorId
) {
}
