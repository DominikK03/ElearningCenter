package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.input.UpdateLessonInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class UpdateLessonUseCase {
    private final CourseRepository courseRepository;

    public UpdateLessonUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void execute(UpdateLessonInput command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        course.ensureOwnedBy(command.instructorId());
        Section section = course.findSection(command.sectionId());
        Lesson lesson = section.findLesson(command.lessonId());
        lesson.updateTitle(command.title());
        lesson.updateContent(command.content());
        lesson.updateOrderIndex(command.orderIndex());
    }
}
