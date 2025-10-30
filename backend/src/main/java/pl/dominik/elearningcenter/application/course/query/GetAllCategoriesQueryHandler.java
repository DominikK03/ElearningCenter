package pl.dominik.elearningcenter.application.course.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.CourseRepository;

import java.util.List;

@Service
public class GetAllCategoriesQueryHandler {
    private final CourseRepository courseRepository;

    public GetAllCategoriesQueryHandler(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<String> handle() {
        return courseRepository.findAllDistinctCategories();
    }
}
