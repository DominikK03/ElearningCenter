package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.DeleteLessonCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class DeleteLessonCommandHandler {
    private final CourseRepository courseRepository;

    public DeleteLessonCommandHandler(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(DeleteLessonCommand command){
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );
        Section section = course.findSection(command.sectionId());
        section.removeLesson(command.lessonId());
    }
}
