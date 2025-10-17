package pl.dominik.elearningcenter.application.course.command;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.input.CreateCourseInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseDescription;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseTitle;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;

@Service
public class CreateCourseUseCase {
    private final CourseRepository courseRepository;

    public CreateCourseUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long execute(CreateCourseInput command){
        CourseTitle courseTitle = new CourseTitle(command.title());
        CourseDescription courseDescription = new CourseDescription(command.description());
        Money money = Money.of(command.price(), command.currency());

        Course course = Course.create(courseTitle, courseDescription, money, command.category(), command.level(), command.instructorId());
        courseRepository.save(course);
        return course.getId();
    }
}
