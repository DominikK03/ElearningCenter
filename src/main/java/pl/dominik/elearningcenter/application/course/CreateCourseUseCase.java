package pl.dominik.elearningcenter.application.course;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.CreateCourseCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseDescription;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseTitle;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;

@Service
@Transactional
public class CreateCourseUseCase {
    private final CourseRepository courseRepository;

    public CreateCourseUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public CourseDTO execute(CreateCourseCommand command){
        CourseTitle courseTitle = new CourseTitle(command.title());
        CourseDescription courseDescription = new CourseDescription(command.description());
        Money money = Money.of(command.price(), command.currency());

        Course course = Course.create(courseTitle, courseDescription, money, command.category(), command.level(), command.instructorId());
        Course savedCourse = courseRepository.save(course);
        return CourseDTO.from(savedCourse);
    }
}
