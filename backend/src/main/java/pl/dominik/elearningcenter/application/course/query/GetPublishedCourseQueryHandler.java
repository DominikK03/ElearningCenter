package pl.dominik.elearningcenter.application.course.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.dto.PublicCourseDTO;
import pl.dominik.elearningcenter.application.course.dto.PagedPublicCoursesDTO;
import pl.dominik.elearningcenter.application.course.mapper.PublicCourseMapper;
import pl.dominik.elearningcenter.application.course.query.GetPublishedCoursesQuery;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.persistence.course.CourseSpecifications;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetPublishedCourseQueryHandler {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PublicCourseMapper publicCourseMapper;

    public GetPublishedCourseQueryHandler(
            CourseRepository courseRepository,
            UserRepository userRepository,
            PublicCourseMapper publicCourseMapper
    ){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.publicCourseMapper = publicCourseMapper;
    }

    @Transactional(readOnly = true)
    public PagedPublicCoursesDTO handle(GetPublishedCoursesQuery command){
        Pageable pageable = PageRequest.of(command.page(), command.size());

        Specification<Course> spec = CourseSpecifications.publishedCoursesWithFilters(
                command.category(),
                command.level()
        );

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);

        Set<Long> instructorIds = coursePage.getContent().stream()
                .map(Course::getInstructorId)
                .collect(Collectors.toSet());

        Map<Long, User> instructorMap = userRepository.findAllById(instructorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<PublicCourseDTO> courses = coursePage.getContent()
                .stream()
                .map(course -> {
                    User instructor = instructorMap.get(course.getInstructorId());
                    return publicCourseMapper.toPublicDto(course, instructor);
                })
                .toList();

        return new PagedPublicCoursesDTO(
                courses,
                coursePage.getNumber(),
                coursePage.getTotalPages(),
                coursePage.getTotalElements()
        );
    }
}
