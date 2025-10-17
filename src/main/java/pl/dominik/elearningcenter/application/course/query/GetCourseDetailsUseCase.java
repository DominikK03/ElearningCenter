package pl.dominik.elearningcenter.application.course.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;

@Service
public class GetCourseDetailsUseCase {
    private final CourseRepository courseRepository;

    public GetCourseDetailsUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public CourseDTO execute(Long courseId){
        Course course = courseRepository.findByIdOrThrow(courseId);
        return CourseDTO.from(course);
    }
}
