package pl.dominik.elearningcenter.application.course.dto;

import java.util.List;

public record SectionDTO (
        Long id,
        String title,
        Integer orderIndex,
        List<LessonDTO> lessons
){
}
