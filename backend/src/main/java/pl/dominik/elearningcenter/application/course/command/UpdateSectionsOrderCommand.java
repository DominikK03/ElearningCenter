package pl.dominik.elearningcenter.application.course.command;

import java.util.Map;

public record UpdateSectionsOrderCommand(
        Long courseId,
        Map<Long, Integer> sectionOrderMap,
        Long instructorId
) {
}
