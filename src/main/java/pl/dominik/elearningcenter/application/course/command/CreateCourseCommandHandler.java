package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.CreateCourseCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseDescription;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseTitle;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;

@Service
public class CreateCourseCommandHandler {
    private final CourseRepository courseRepository;

    public CreateCourseCommandHandler(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long handle(CreateCourseCommand command){
        CourseTitle courseTitle = new CourseTitle(command.title());
        CourseDescription courseDescription = new CourseDescription(command.description());
        Money money = Money.of(command.price(), command.currency());

        Course course = Course.create(courseTitle, courseDescription, money, command.category(), command.level(), command.instructorId());
        courseRepository.save(course);
        return course.getId();
    }
}
