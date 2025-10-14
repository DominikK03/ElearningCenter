package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.input.DeleteLessonInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class DeleteLessonUseCase {
    private final CourseRepository courseRepository;

    public DeleteLessonUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(DeleteLessonInput command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        course.ensureOwnedBy(command.instructorId());
        Section section = course.findSection(command.sectionId());
        section.removeLesson(command.lessonId());
    }
}
