package pl.dominik.elearningcenter.interfaces.rest.course.request;

import java.util.Map;

public record UpdateSectionsOrderRequest(
        Map<Long, Integer> sectionOrderMap // sectionId -> newOrderIndex
) {
}
