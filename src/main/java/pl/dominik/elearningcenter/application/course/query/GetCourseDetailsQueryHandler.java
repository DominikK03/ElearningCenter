package pl.dominik.elearningcenter.application.course.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.mapper.CourseMapper;
import pl.dominik.elearningcenter.domain.course.CourseRepository;

import java.util.Optional;

@Service
public class GetCourseDetailsQueryHandler {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public GetCourseDetailsQueryHandler(CourseRepository courseRepository, CourseMapper courseMapper){
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public Optional<CourseDTO> handle(Long courseId){
        return courseRepository.findById(courseId)
                .map(courseMapper::toDto);
    }
}
