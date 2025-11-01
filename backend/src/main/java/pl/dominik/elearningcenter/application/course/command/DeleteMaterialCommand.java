package pl.dominik.elearningcenter.application.course.command;

public record DeleteMaterialCommand(
        Long courseId,
        Long sectionId,
        Long lessonId,
        Long materialId,
        Long instructorId
) {
}
