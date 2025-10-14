package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.input.AddLessonInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class AddLessonUseCase {
    private final CourseRepository courseRepository;

    public AddLessonUseCase(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long execute(AddLessonInput command) {
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        if (!course.isOwnedBy(command.instructorId())) {
            throw new DomainException("Only course owner can add lesson to this course");
        }

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
