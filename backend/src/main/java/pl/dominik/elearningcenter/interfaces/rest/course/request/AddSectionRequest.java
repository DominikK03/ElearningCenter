package pl.dominik.elearningcenter.interfaces.rest.course.request;

public record AddSectionRequest(
        String title,
        Integer orderIndex
) {
}
