package pl.dominik.elearningcenter.interfaces.rest.course.dto;

public record AddSectionRequest(
        String title,
        Integer orderIndex
) {
}
