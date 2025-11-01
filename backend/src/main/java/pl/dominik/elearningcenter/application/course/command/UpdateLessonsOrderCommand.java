package pl.dominik.elearningcenter.application.course.command;

import java.util.Map;

public record UpdateLessonsOrderCommand(
        Long courseId,
        Long sectionId,
        Map<Long, Integer> lessonOrderMap,
        Long instructorId
) {
}
