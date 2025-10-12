package pl.dominik.elearningcenter.application.course.command;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;

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
