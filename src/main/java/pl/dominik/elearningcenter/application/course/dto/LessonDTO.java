package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Material;
import pl.dominik.elearningcenter.domain.course.Section;

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
    public static LessonDTO from(Lesson lesson){
        return new LessonDTO(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                lesson.getDurationMinutes(),
                lesson.getOrderIndex(),
                lesson.getMaterials().stream()
                        .map(MaterialDTO::from)
                        .toList()
        );
    }
}
