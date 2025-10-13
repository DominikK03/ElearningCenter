package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UpdateSectionUseCase {
    private final CourseRepository courseRepository;

    public UpdateSectionUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(UpdateSectionCommand command){
        Course course = courseRepository.findById(command.courseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + command.courseId()));
        if (!course.isOwnedBy(command.instructorId())){
            throw new DomainException("Only course owner can update this course");
        }

        Section section = course.findSection(command.sectionId());
        section.updateTitle(command.title());
        section.updateOrderIndex(command.orderIndex());
    }
}
