package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;

@Service
public class UpdateLessonsOrderCommandHandler {
    private final CourseRepository courseRepository;

    public UpdateLessonsOrderCommandHandler(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(UpdateLessonsOrderCommand command) {
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );

        Section section = course.findSection(command.sectionId());

        command.lessonOrderMap().forEach((lessonId, newOrderIndex) -> {
            Lesson lesson = section.findLesson(lessonId);
            lesson.updateOrderIndex(newOrderIndex);
        });

        courseRepository.save(course);
    }
}
