package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.SectionDTO;
import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String currency,
        String thumbnailUrl,
        String category,
        CourseLevel level,
        Long instructorId,
        boolean published,
        LocalDateTime createdAt,
        List<SectionDTO> sections,
        int sectionsCount,
        int totalLessonsCount
) {
    public static CourseResponse from(CourseDTO dto){
        return new CourseResponse(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.thumbnailUrl(),
                dto.category(),
                dto.level(),
                dto.instructorId(),
                dto.published(),
                dto.createdAt(),
                dto.sections(),
                dto.sectionsCount(),
                dto.totalLessonsCount()
        );
    }
}
