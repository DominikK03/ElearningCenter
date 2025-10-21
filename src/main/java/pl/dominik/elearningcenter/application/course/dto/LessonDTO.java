package pl.dominik.elearningcenter.application.course.dto;

import java.util.List;

/**
 * Data Transfer Object for Lesson.
 * Pure data structure without any mapping logic.
 * Mapping is handled by LessonMapper component.
 */
public record LessonDTO(
        Long id,
        String title,
        String content,
        String videoUrl,
        Integer durationMinutes,
        Integer orderIndex,
        List<MaterialDTO> materials
) {
}
