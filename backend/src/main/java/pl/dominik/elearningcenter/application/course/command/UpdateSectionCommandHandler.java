package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UpdateSectionCommandHandler {
    private final CourseRepository courseRepository;

    public UpdateSectionCommandHandler(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(UpdateSectionCommand command){
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );

        Section section = course.findSection(command.sectionId());
        section.updateTitle(command.title());
        section.updateOrderIndex(command.orderIndex());
    }
}
