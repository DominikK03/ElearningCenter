package pl.dominik.elearningcenter.interfaces.rest.course.request;

public record UpdateLessonRequest(
        String title,
        String content,
        String videoUrl,
        Integer durationMinutes
) {
}
