package pl.dominik.elearningcenter.application.course.input;

public record AddLessonInput(
        Long courseId,
        Long sectionId,
        String title,
        String content,
        Integer orderIndex,
        Long instructorId
) {
}
