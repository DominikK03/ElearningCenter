package pl.dominik.elearningcenter.interfaces.rest.course.request;

import java.util.Map;

public record UpdateLessonsOrderRequest(
        Map<Long, Integer> lessonOrderMap // lessonId -> newOrderIndex
) {
}
