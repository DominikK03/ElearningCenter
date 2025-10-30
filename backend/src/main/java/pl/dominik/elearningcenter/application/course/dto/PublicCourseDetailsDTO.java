package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PublicCourseDetailsDTO(
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
}
