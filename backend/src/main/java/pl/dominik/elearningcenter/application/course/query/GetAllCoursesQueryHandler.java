package pl.dominik.elearningcenter.application.course.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.mapper.CourseMapper;
import pl.dominik.elearningcenter.application.course.query.GetAllCoursesQuery;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;

import java.util.List;

@Service
public class GetAllCoursesQueryHandler {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public GetAllCoursesQueryHandler(CourseRepository courseRepository, CourseMapper courseMapper){
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public PagedCoursesDTO handle(GetAllCoursesQuery command){
        Pageable pageable = PageRequest.of(command.page(), command.size());
        Page<Course> coursePage = courseRepository.findAll(pageable);

        List<CourseDTO> courses = coursePage.getContent()
                .stream()
                .map(courseMapper::toDtoSummary)
                .toList();

        return new PagedCoursesDTO(
                courses,
                coursePage.getNumber(),
                coursePage.getTotalPages(),
                coursePage.getTotalElements()
        );
    }
}
