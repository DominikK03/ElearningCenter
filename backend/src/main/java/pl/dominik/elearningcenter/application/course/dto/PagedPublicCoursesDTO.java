package pl.dominik.elearningcenter.application.course.dto;

import java.util.List;

public record PagedPublicCoursesDTO(
        List<PublicCourseDTO> courses,
        int currentPage,
        int totalPages,
        long totalElements
) {

}
