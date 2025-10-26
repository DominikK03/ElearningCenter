package pl.dominik.elearningcenter.application.course.dto;

import java.util.List;

/**
 * Data Transfer Object for Section.
 * Pure data structure without any mapping logic.
 * Mapping is handled by SectionMapper component.
 */
public record SectionDTO (
        Long id,
        String title,
        Integer orderIndex,
        List<LessonDTO> lessons
){
}
