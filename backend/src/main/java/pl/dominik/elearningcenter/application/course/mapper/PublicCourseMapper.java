package pl.dominik.elearningcenter.application.course.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.course.dto.PublicCourseDTO;
import pl.dominik.elearningcenter.application.course.dto.PublicCourseDetailsDTO;
import pl.dominik.elearningcenter.application.course.dto.PublicSectionDTO;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.user.User;

import java.util.List;

@Component
public class PublicCourseMapper {

    public PublicCourseDTO toPublicDto(Course course, User instructor) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (instructor == null) {
            throw new IllegalArgumentException("Instructor cannot be null for course: " + course.getId());
        }

        return new PublicCourseDTO(
                course.getId(),
                course.getTitle().getValue(),
                course.getDescription().getValue(),
                course.getPrice().getAmount(),
                course.getPrice().getCurrency().getCurrencyCode(),
                course.getThumbnailUrl(),
                course.getCategory(),
                course.getLevel(),
                instructor.getUsername().getValue(),
                course.isPublished(),
                course.getCreatedAt(),
                course.getSectionsCount(),
                course.getTotalLessonsCount()
        );
    }

    public PublicCourseDetailsDTO toPublicDetailsDto(Course course, User instructor) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (instructor == null) {
            throw new IllegalArgumentException("Instructor cannot be null for course: " + course.getId());
        }

        List<PublicSectionDTO> sections = course.getSections().stream()
                .map(section -> new PublicSectionDTO(
                        section.getId(),
                        section.getTitle(),
                        section.getOrderIndex()
                ))
                .toList();

        return new PublicCourseDetailsDTO(
                course.getId(),
                course.getTitle().getValue(),
                course.getDescription().getValue(),
                course.getPrice().getAmount(),
                course.getPrice().getCurrency().getCurrencyCode(),
                course.getThumbnailUrl(),
                course.getCategory(),
                course.getLevel(),
                instructor.getUsername().getValue(),
                course.isPublished(),
                course.getCreatedAt(),
                sections,
                course.getSectionsCount(),
                course.getTotalLessonsCount()
        );
    }
}
