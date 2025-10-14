package pl.dominik.elearningcenter.application.course;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.input.AddSectionInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;

@Service
public class AddSectionUseCase {
    private final CourseRepository courseRepository;

    public AddSectionUseCase(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long execute(AddSectionInput command){
        Course course = courseRepository.findByIdOrThrow(command.courseId());
        Section section = new Section(command.title(), command.orderIndex());
        course.addSection(section);
        return section.getId();
    }
}
