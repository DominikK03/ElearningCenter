package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
import pl.dominik.elearningcenter.application.course.dto.MaterialDTO;

import java.util.List;

public record LessonResponse(
        Long id,
        String title,
        String content,
        String videoUrl,
        Integer durationMinutes,
        Integer orderIndex,
        List<MaterialDTO> materials
) {
    public static LessonResponse from(LessonDTO dto){
        return new LessonResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.videoUrl(),
                dto.durationMinutes(),
                dto.orderIndex(),
                dto.materials()
        );
    }
}
