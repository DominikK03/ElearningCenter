package pl.dominik.elearningcenter.application.course.command;

public record AddSectionCommand(
        Long courseId,
        String title,
        Integer orderIndex
) {

}
