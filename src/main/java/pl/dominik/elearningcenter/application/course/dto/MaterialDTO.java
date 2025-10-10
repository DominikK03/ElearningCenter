package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Material;
import pl.dominik.elearningcenter.domain.course.MaterialType;

public record MaterialDTO(
        Long id,
        String title,
        String fileUrl,
        MaterialType fileType
) {
    public static MaterialDTO from(Material material){
        return new MaterialDTO(
                material.getId(),
                material.getTitle(),
                material.getFileUrl(),
                material.getFileType()
        );
    }
}
