package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.CourseDTO;

import java.time.LocalDateTime;

public record PublishCourseResponse(
        Long id,
        boolean published,
        LocalDateTime publishedAt
) {
    public static PublishCourseResponse from(CourseDTO dto) {
        return new PublishCourseResponse(
                dto.id(),
                dto.published(),
                dto.createdAt()
        );
    }
}
