package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
import pl.dominik.elearningcenter.application.course.dto.SectionDTO;

import java.util.List;

public record SectionResponse(
        Long id,
        String title,
        Integer orderIndex,
        List<LessonDTO> lessons
) {
    public static SectionResponse from(SectionDTO dto){
        return new SectionResponse(
                dto.id(),
                dto.title(),
                dto.orderIndex(),
                dto.lessons()
        );
    }
}
