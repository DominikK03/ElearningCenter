package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.input.DeleteSectionInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class DeleteSectionUseCase {
    private final CourseRepository courseRepository;

    public DeleteSectionUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(DeleteSectionInput command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        course.ensureOwnedBy(command.instructorId());
        course.removeSection(command.sectionId());
    }
}
