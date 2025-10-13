package pl.dominik.elearningcenter.application.course;

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
public class UpdateCourseUseCase {
    private final CourseRepository courseRepository;

    public UpdateCourseUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(UpdateCourseCommand command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        if (!course.isOwnedBy(command.instructorId())){
            throw new DomainException("Only course owner can update the course");
        }
        course.updateTitle(new CourseTitle(command.title()));
        course.updateDescription(new CourseDescription(command.description()));
        course.updatePrice(Money.of(command.priceAmount(), command.priceCurrency()));
        course.updateCategory(command.category());
        course.updateLevel(command.level());
    }
}
