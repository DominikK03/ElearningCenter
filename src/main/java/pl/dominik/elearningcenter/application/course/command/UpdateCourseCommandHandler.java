package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UpdateCourseCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseDescription;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseTitle;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;

@Service
public class UpdateCourseCommandHandler {
    private final CourseRepository courseRepository;

    public UpdateCourseCommandHandler(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(UpdateCourseCommand command){
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );
        course.updateTitle(new CourseTitle(command.title()));
        course.updateDescription(new CourseDescription(command.description()));
        course.updatePrice(Money.of(command.priceAmount(), command.priceCurrency()));
        course.updateCategory(command.category());
        course.updateLevel(command.level());
    }
}
