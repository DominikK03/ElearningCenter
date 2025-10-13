package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;

import java.util.List;

public record PagedCoursesResponse(
        List<CourseResponse> courses,
        int currentPage,
        int totalPages,
        long totalElements
){
    public static PagedCoursesResponse from(PagedCoursesDTO dto){
        List<CourseResponse> courses = dto.courses().stream()
                .map(CourseResponse::from)
                .toList();

        return new PagedCoursesResponse(
                courses,
                dto.currentPage(),
                dto.totalPages(),
                dto.totalElements()
        );
    }
}
