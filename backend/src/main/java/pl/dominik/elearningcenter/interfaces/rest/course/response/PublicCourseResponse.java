package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.PublicCourseDTO;
import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PublicCourseResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String currency,
        String thumbnailUrl,
        String category,
        CourseLevel level,
        String instructorName,
        boolean published,
        LocalDateTime createdAt,
        int sectionsCount,
        int totalLessonsCount
) {
    public static PublicCourseResponse from(PublicCourseDTO dto){
        return new PublicCourseResponse(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.thumbnailUrl(),
                dto.category(),
                dto.level(),
                dto.instructorName(),
                dto.published(),
                dto.createdAt(),
                dto.sectionsCount(),
                dto.totalLessonsCount()
        );
    }
}
