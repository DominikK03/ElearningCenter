package pl.dominik.elearningcenter.application.course.input;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;

public record CreateCourseInput(
        String title,
        String description,
        BigDecimal price,
        String currency,
        Long instructorId,
        String category,
        CourseLevel level
) {
}
