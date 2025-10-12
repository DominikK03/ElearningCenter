package pl.dominik.elearningcenter.application.course;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
@Transactional
public class PublishCourseUseCase {
    private final CourseRepository courseRepository;

    public PublishCourseUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public CourseDTO execute(Long courseId){
        Course course = courseRepository.findByIdOrThrow(courseId);
        course.publish();
        Course publishedCourse = courseRepository.save(course);
        return CourseDTO.from(publishedCourse);
    }
}
