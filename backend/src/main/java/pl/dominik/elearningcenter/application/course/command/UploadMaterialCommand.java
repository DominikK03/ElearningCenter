package pl.dominik.elearningcenter.application.course.command;

import org.springframework.web.multipart.MultipartFile;

public record UploadMaterialCommand(
        Long courseId,
        Long sectionId,
        Long lessonId,
        String title,
        MultipartFile file,
        Long instructorId
) {
}
