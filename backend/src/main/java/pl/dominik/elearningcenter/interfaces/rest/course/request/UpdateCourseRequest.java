package pl.dominik.elearningcenter.interfaces.rest.course.request;

import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;

public record UpdateCourseRequest(
        String title,
        String description,
        BigDecimal price,
        String currency,
        String category,
        CourseLevel level
) {
}
