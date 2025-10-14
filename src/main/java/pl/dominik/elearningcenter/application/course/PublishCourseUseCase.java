package pl.dominik.elearningcenter.application.course;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class PublishCourseUseCase {
    private final CourseRepository courseRepository;

    public PublishCourseUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public CourseDTO execute(Long courseId, Long instructorId){
        Course course = courseRepository.findByIdOrThrow(courseId);
        course.ensureOwnedBy(instructorId);
        course.publish();
        Course publishedCourse = courseRepository.save(course);
        return CourseDTO.from(publishedCourse);
    }
}
