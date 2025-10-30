package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.MaterialType;

public record MaterialDTO(
        Long id,
        String title,
        String fileUrl,
        MaterialType fileType
) {
}
