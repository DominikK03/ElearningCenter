package pl.dominik.elearningcenter.application.course.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;
import pl.dominik.elearningcenter.application.course.input.GetCoursesByInstructorInput;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;

import java.util.List;

@Service
public class GetCoursesByInstructorUseCase {
    private final CourseRepository courseRepository;

    public GetCoursesByInstructorUseCase(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public PagedCoursesDTO execute(GetCoursesByInstructorInput command) {
        Pageable pageable = PageRequest.of(command.page(), command.size());

        Page<Course> coursePage = courseRepository.findByInstructorId(command.instructorId(), pageable);

        List<CourseDTO> courses = coursePage.getContent()
                .stream()
                .map(CourseDTO::from)
                .toList();

        return new PagedCoursesDTO(
                courses,
                coursePage.getNumber(),
                coursePage.getTotalPages(),
                coursePage.getTotalElements()
        );
    }
}