package pl.dominik.elearningcenter.application.course.dto;

import java.util.List;

public record PagedCoursesDTO(
        List<CourseDTO> courses,
        int currentPage,
        int totalPages,
        long totalElements
) {

}
