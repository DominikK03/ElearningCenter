package pl.dominik.elearningcenter.application.course.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;
import pl.dominik.elearningcenter.application.course.mapper.CourseMapper;
import pl.dominik.elearningcenter.application.course.query.GetCoursesByInstructorQuery;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;

import java.util.List;

@Service
public class GetCoursesByInstructorQueryHandler {
    private static final Logger log = LoggerFactory.getLogger(GetCoursesByInstructorQueryHandler.class);
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public GetCoursesByInstructorQueryHandler(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional(readOnly = true)
    public PagedCoursesDTO handle(GetCoursesByInstructorQuery command) {
        log.info("Fetching courses for instructor: {}, page: {}, size: {}",
                command.instructorId(), command.page(), command.size());

        Pageable pageable = PageRequest.of(command.page(), command.size());

        Page<Course> coursePage = courseRepository.findByInstructorId(command.instructorId(), pageable);

        log.info("Found {} courses for instructor {}", coursePage.getTotalElements(), command.instructorId());

        List<CourseDTO> courses = coursePage.getContent()
                .stream()
                .map(course -> {
                    try {
                        log.debug("Mapping course: id={}, title={}", course.getId(), course.getTitle().getValue());
                        return courseMapper.toDtoSummary(course);
                    } catch (Exception e) {
                        log.error("Error mapping course {}: {}", course.getId(), e.getMessage(), e);
                        throw e;
                    }
                })
                .toList();

        return new PagedCoursesDTO(
                courses,
                coursePage.getNumber(),
                coursePage.getTotalPages(),
                coursePage.getTotalElements()
        );
    }
}
