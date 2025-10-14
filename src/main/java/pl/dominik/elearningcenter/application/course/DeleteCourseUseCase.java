package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.input.DeleteCourseInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class DeleteCourseUseCase {
    private final CourseRepository courseRepository;

    public DeleteCourseUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(DeleteCourseInput command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        course.ensureOwnedBy(command.instructorId());
        courseRepository.delete(course);
    }
}
