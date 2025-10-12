package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.DeleteSectionCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotFoundException;
import pl.dominik.elearningcenter.domain.course.exception.SectionNotFoundException;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class DeleteSectionUseCase {
    private final CourseRepository courseRepository;

    public DeleteSectionUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(DeleteSectionCommand command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        if (!course.isOwnedBy(command.instructorId())){
            throw new DomainException("Only course owner can delete sections");
        }
        course.removeSection(command.sectionId());
    }
}
