package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;

@Service
public class UpdateSectionsOrderCommandHandler {
    private final CourseRepository courseRepository;

    public UpdateSectionsOrderCommandHandler(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(UpdateSectionsOrderCommand command) {
        Course course = courseRepository.findByIdAndInstructorIdOrThrow(
                command.courseId(),
                command.instructorId()
        );

        command.sectionOrderMap().forEach((sectionId, newOrderIndex) -> {
            Section section = course.findSection(sectionId);
            section.updateOrderIndex(newOrderIndex);
        });

        courseRepository.save(course);
    }
}
