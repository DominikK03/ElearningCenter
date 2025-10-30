package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.PagedPublicCoursesDTO;

import java.util.List;

public record PagedPublicCoursesResponse(
        List<PublicCourseResponse> courses,
        int currentPage,
        int totalPages,
        long totalElements
){
    public static PagedPublicCoursesResponse from(PagedPublicCoursesDTO dto){
        List<PublicCourseResponse> courses = dto.courses().stream()
                .map(PublicCourseResponse::from)
                .toList();

        return new PagedPublicCoursesResponse(
                courses,
                dto.currentPage(),
                dto.totalPages(),
                dto.totalElements()
        );
    }
}
