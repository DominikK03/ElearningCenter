package pl.dominik.elearningcenter.application.course;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.command.AddSectionCommand;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;

@Service
@Transactional
public class AddSectionUseCase {
    private final CourseRepository courseRepository;

    public AddSectionUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public CourseDTO execute(AddSectionCommand command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        Section section = new Section(command.title(), command.orderIndex());
        course.addSection(section);
        Course savedCourse = courseRepository.save(course);
        return CourseDTO.from(savedCourse);
    }
}
