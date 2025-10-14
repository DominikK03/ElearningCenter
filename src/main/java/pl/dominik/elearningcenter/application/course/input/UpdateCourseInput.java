package pl.dominik.elearningcenter.application.course.input;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;

public record UpdateCourseInput(
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
