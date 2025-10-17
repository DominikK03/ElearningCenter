package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.input.UnpublishCourseInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UnpublishCourseUseCase {
    private final CourseRepository courseRepository;

    public UnpublishCourseUseCase(CourseRepository courseRepository)
    {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(UnpublishCourseInput command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        if (!course.isOwnedBy(command.instructorId())){
            throw new DomainException("Only course owner can unpublish the course");
        }
        course.unpublish();
    }
}
