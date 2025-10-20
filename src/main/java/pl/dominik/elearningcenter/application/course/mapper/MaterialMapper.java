package pl.dominik.elearningcenter.application.course.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.course.dto.MaterialDTO;
import pl.dominik.elearningcenter.domain.course.Material;

@Component
public class MaterialMapper {

    public MaterialDTO toDto(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }

        return new MaterialDTO(
                material.getId(),
                material.getTitle(),
                material.getFileUrl(),
                material.getFileType()
        );
    }
}
