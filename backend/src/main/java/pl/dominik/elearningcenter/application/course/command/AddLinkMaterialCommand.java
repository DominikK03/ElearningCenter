package pl.dominik.elearningcenter.application.course.command;

public record AddLinkMaterialCommand(
        Long courseId,
        Long sectionId,
        Long lessonId,
        String title,
        String url,
        Long instructorId
) {}
