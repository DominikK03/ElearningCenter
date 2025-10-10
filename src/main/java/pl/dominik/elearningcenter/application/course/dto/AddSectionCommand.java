package pl.dominik.elearningcenter.application.course.dto;

public record AddSectionCommand(
        Long courseId,
        String title,
        Integer orderIndex
) {

}
