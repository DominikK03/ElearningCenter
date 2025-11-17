package pl.dominik.elearningcenter.application.course.command;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.mapper.CourseMapper;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class PublishCourseCommandHandler {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public PublishCourseCommandHandler(CourseRepository courseRepository, CourseMapper courseMapper){
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional
    public CourseDTO handle(PublishCourseCommand command){
        if (command.isAdmin()) {
            throw new DomainException("Administrators cannot publish courses");
        }
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.actorId()
        );
        course.publish();
        Course publishedCourse = courseRepository.save(course);
        return courseMapper.toDto(publishedCourse);
    }
}
