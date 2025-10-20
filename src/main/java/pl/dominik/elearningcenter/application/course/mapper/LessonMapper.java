package pl.dominik.elearningcenter.application.course.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
import pl.dominik.elearningcenter.application.course.dto.MaterialDTO;
import pl.dominik.elearningcenter.domain.course.Lesson;

import java.util.List;


@Component
public class LessonMapper {

    private final MaterialMapper materialMapper;

    public LessonMapper(MaterialMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    public LessonDTO toDto(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        List<MaterialDTO> materials = lesson.getMaterials().stream()
                .map(materialMapper::toDto)
                .toList();

        return new LessonDTO(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                lesson.getDurationMinutes(),
                lesson.getOrderIndex(),
                materials
        );
    }

    public LessonDTO toDtoSummary(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        return new LessonDTO(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                lesson.getDurationMinutes(),
                lesson.getOrderIndex(),
                List.of()
        );
    }
}
