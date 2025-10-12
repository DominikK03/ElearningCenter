package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.DeleteLessonCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class DeleteLessonUseCase {
    private final CourseRepository courseRepository;

    public DeleteLessonUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(DeleteLessonCommand command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        if (!course.isOwnedBy(command.instructorId())){
            throw new DomainException("Only course owner can modify it!");
        }
        Section section = course.findSection(command.sectionId());
        section.removeLesson(command.lessonId());
    }
}
