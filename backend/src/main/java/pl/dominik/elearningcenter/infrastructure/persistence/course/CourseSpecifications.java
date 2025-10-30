package pl.dominik.elearningcenter.infrastructure.persistence.course;

import org.springframework.data.jpa.domain.Specification;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;

public class CourseSpecifications {

    public static Specification<Course> isPublished(boolean published) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("published"), published);
    }

    public static Specification<Course> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    public static Specification<Course> hasLevel(CourseLevel level) {
        return (root, query, criteriaBuilder) -> {
            if (level == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("level"), level);
        };
    }

    public static Specification<Course> hasInstructor(Long instructorId) {
        return (root, query, criteriaBuilder) -> {
            if (instructorId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("instructorId"), instructorId);
        };
    }

    public static Specification<Course> publishedCoursesWithFilters(String category, CourseLevel level) {
        return isPublished(true)
                .and(hasCategory(category))
                .and(hasLevel(level));
    }
}
