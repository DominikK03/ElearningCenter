package pl.dominik.elearningcenter.application.course.dto;

import pl.dominik.elearningcenter.domain.course.MaterialType;

/**
 * Data Transfer Object for Material.
 * Pure data structure without any mapping logic.
 * Mapping is handled by MaterialMapper component.
 */
public record MaterialDTO(
        Long id,
        String title,
        String fileUrl,
        MaterialType fileType
) {
}
