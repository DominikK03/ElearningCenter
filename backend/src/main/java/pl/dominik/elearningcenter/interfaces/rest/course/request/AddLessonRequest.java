package pl.dominik.elearningcenter.interfaces.rest.course.request;

public record AddLessonRequest(
        String title,
        String content,
        Integer orderIndex
) {
}
