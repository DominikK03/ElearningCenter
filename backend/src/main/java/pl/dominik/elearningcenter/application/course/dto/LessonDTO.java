package pl.dominik.elearningcenter.application.course.dto;

import java.util.List;

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
