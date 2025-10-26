package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UpdateLessonCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UpdateLessonCommandHandler {
    private final CourseRepository courseRepository;

    public UpdateLessonCommandHandler(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(UpdateLessonCommand command){
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );
        Section section = course.findSection(command.sectionId());
        Lesson lesson = section.findLesson(command.lessonId());
        lesson.updateTitle(command.title());
        lesson.updateContent(command.content());
        lesson.updateOrderIndex(command.orderIndex());
    }
}
