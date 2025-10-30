package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.PublicCourseDetailsDTO;
import pl.dominik.elearningcenter.application.course.dto.PublicSectionDTO;
import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PublicCourseDetailsResponse(
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
        List<PublicSectionDTO> sections,
        int sectionsCount,
        int totalLessonsCount
) {
    public static PublicCourseDetailsResponse from(PublicCourseDetailsDTO dto){
        return new PublicCourseDetailsResponse(
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
                dto.sections(),
                dto.sectionsCount(),
                dto.totalLessonsCount()
        );
    }
}
