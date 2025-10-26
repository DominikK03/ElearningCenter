package pl.dominik.elearningcenter.application.course.command;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;

public record UpdateCourseCommand(
        Long courseId,
        String title,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        String category,
        CourseLevel level,
        Long instructorId
) {
}
