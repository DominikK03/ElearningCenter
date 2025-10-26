package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.AddLessonCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class AddLessonCommandHandler {
    private final CourseRepository courseRepository;

    public AddLessonCommandHandler(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long handle(AddLessonCommand command) {
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );

        Section section = course.findSection(command.sectionId());
        Lesson newLesson = new Lesson(
                command.title(),
                command.content(),
                command.orderIndex()
        );
        section.addLesson(newLesson);
        return newLesson.getId();
    }
}
