package pl.dominik.elearningcenter.application.course.input;

public record UpdateLessonInput(
        Long courseId,
        Long sectionId,
        Long lessonId,
        String title,
        String content,
        Integer orderIndex,
        Long instructorId
) {
}
