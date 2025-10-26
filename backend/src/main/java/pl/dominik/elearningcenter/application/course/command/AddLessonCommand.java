package pl.dominik.elearningcenter.application.course.command;

public record AddLessonCommand(
        Long courseId,
        Long sectionId,
        String title,
        String content,
        Integer orderIndex,
        Long instructorId
) {
}
