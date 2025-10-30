package pl.dominik.elearningcenter.application.course.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.dto.PublicCourseDetailsDTO;
import pl.dominik.elearningcenter.application.course.mapper.PublicCourseMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

import java.util.Optional;

@Service
public class GetCourseDetailsQueryHandler {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PublicCourseMapper publicCourseMapper;

    public GetCourseDetailsQueryHandler(
            CourseRepository courseRepository,
            UserRepository userRepository,
            PublicCourseMapper publicCourseMapper
    ){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.publicCourseMapper = publicCourseMapper;
    }

    @Transactional(readOnly = true)
    public Optional<PublicCourseDetailsDTO> handle(Long courseId){
        Optional<Course> courseOpt = courseRepository.findWithSectionsById(courseId);

        if (courseOpt.isEmpty()) {
            return Optional.empty();
        }

        Course course = courseOpt.get();
        User instructor = userRepository.findByIdOrThrow(course.getInstructorId());

        return Optional.of(publicCourseMapper.toPublicDetailsDto(course, instructor));
    }
}
