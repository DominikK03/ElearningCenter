package pl.dominik.elearningcenter.application.course.command;

public record UpdateSectionCommand(
        Long courseId,
        Long sectionId,
        String title,
        Integer orderIndex,
        Long instructorId
) {
}
