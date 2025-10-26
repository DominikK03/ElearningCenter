package pl.dominik.elearningcenter.application.course.command;

public record DeleteSectionCommand(
        Long courseId,
        Long sectionId,
        Long instructorId
) {
}
