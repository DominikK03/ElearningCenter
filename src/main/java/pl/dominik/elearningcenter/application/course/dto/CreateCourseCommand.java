package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.util.Currency;

public record CreateCourseCommand(
        String title,
        String description,
        BigDecimal price,
        String currency,
        Long instructorId,
        String category,
        CourseLevel level
) {
}
