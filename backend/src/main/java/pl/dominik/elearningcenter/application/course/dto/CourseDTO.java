package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseDTO(
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
}
