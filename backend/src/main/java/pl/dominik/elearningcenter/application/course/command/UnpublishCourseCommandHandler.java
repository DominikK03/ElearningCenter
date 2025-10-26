package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UnpublishCourseCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UnpublishCourseCommandHandler {
    private final CourseRepository courseRepository;

    public UnpublishCourseCommandHandler(CourseRepository courseRepository)
    {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(UnpublishCourseCommand command){
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );
        course.unpublish();
    }
}
