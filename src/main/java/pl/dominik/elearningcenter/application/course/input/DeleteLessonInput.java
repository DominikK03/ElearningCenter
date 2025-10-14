package pl.dominik.elearningcenter.application.course.input;

public record DeleteLessonInput(
        Long courseId,
        Long sectionId,
        Long lessonId,
        Long instructorId
) {
}
