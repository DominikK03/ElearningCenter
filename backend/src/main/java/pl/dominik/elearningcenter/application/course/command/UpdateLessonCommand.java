package pl.dominik.elearningcenter.application.course.command;

public record UpdateLessonCommand(
        Long courseId,
        Long sectionId,
        Long lessonId,
        String title,
        String content,
        String videoUrl,
        Integer durationMinutes,
        Long instructorId
) {
}
