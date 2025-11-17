package pl.dominik.elearningcenter.application.admin.dto;

public record AdminStatsDTO(
        long totalCourses,
        long activeCourses,
        long inactiveCourses,
        long totalUsers
) {
}
