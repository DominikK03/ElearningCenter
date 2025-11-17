package pl.dominik.elearningcenter.application.admin.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.admin.dto.AdminStatsDTO;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.user.UserRepository;

@Service
public class GetAdminStatsQueryHandler {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public GetAdminStatsQueryHandler(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsDTO handle() {
        long totalCourses = courseRepository.count();
        long activeCourses = courseRepository.countByPublished(true);
        long inactiveCourses = totalCourses - activeCourses;
        long totalUsers = userRepository.count();

        return new AdminStatsDTO(
                totalCourses,
                activeCourses,
                inactiveCourses,
                totalUsers
        );
    }
}
