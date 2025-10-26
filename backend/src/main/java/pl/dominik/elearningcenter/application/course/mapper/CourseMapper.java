package pl.dominik.elearningcenter.application.course.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.SectionDTO;
import pl.dominik.elearningcenter.domain.course.Course;

import java.util.List;

@Component
public class CourseMapper {

    private final SectionMapper sectionMapper;

    public CourseMapper(SectionMapper sectionMapper) {
        this.sectionMapper = sectionMapper;
    }

    public CourseDTO toDto(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        List<SectionDTO> sections = course.getSections().stream()
                .map(sectionMapper::toDto)
                .toList();

        return new CourseDTO(
                course.getId(),
                course.getTitle().getValue(),
                course.getDescription().getValue(),
                course.getPrice().getAmount(),
                course.getPrice().getCurrency().getCurrencyCode(),
                course.getThumbnailUrl(),
                course.getCategory(),
                course.getLevel(),
                course.getInstructorId(),
                course.isPublished(),
                course.getCreatedAt(),
                sections,
                course.getSectionsCount(),
                course.getTotalLessonsCount()
        );
    }

    public CourseDTO toDtoWithSectionsSummary(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        List<SectionDTO> sections = course.getSections().stream()
                .map(sectionMapper::toDtoSummary)  // Lessons without materials
                .toList();

        return new CourseDTO(
                course.getId(),
                course.getTitle().getValue(),
                course.getDescription().getValue(),
                course.getPrice().getAmount(),
                course.getPrice().getCurrency().getCurrencyCode(),
                course.getThumbnailUrl(),
                course.getCategory(),
                course.getLevel(),
                course.getInstructorId(),
                course.isPublished(),
                course.getCreatedAt(),
                sections,
                course.getSectionsCount(),
                course.getTotalLessonsCount()
        );
    }

    public CourseDTO toDtoSummary(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        return new CourseDTO(
                course.getId(),
                course.getTitle().getValue(),
                course.getDescription().getValue(),
                course.getPrice().getAmount(),
                course.getPrice().getCurrency().getCurrencyCode(),
                course.getThumbnailUrl(),
                course.getCategory(),
                course.getLevel(),
                course.getInstructorId(),
                course.isPublished(),
                course.getCreatedAt(),
                List.of(),
                course.getSectionsCount(),
                course.getTotalLessonsCount()
        );
    }
}
