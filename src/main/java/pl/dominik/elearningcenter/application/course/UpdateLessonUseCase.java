package pl.dominik.elearningcenter.application.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UpdateLessonCommand;
import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
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
    public LessonDTO execute(UpdateLessonCommand command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        if (!course.isOwnedBy(command.instructorId())){
            throw new DomainException("Only course owner can update lesson");
        }
        Section section = course.findSection(command.sectionId());
        Lesson lesson = section.findLesson(command.lessonId());
        lesson.updateTitle(command.title());
        lesson.updateContent(command.content());
        lesson.updateOrderIndex(command.orderIndex());

        return LessonDTO.from(lesson);

    }
}
