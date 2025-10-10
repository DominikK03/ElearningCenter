package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.Section;

import java.util.List;

public record SectionDTO (
        Long id,
        String title,
        Integer orderIndex,
        List<LessonDTO> lessons
){
    public static SectionDTO from(Section section){
        return new SectionDTO(
                section.getId(),
                section.getTitle(),
                section.getOrderIndex(),
                section.getLessons().stream()
                        .map(LessonDTO::from)
                        .toList()
        );
    }

}
