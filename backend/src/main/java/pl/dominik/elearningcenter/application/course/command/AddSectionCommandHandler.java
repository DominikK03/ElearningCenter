package pl.dominik.elearningcenter.application.course.command;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.command.AddSectionCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;

@Service
public class AddSectionCommandHandler {
    private final CourseRepository courseRepository;

    public AddSectionCommandHandler(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long handle(AddSectionCommand command){
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );

        Section section = new Section(command.title(), command.orderIndex());
        course.addSection(section);
        return section.getId();
    }
}
