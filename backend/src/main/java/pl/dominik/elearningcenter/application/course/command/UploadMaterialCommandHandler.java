package pl.dominik.elearningcenter.application.course.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.*;
import pl.dominik.elearningcenter.infrastructure.storage.FileStorageException;
import pl.dominik.elearningcenter.infrastructure.storage.FileStorageService;

import java.io.IOException;

@Service
public class UploadMaterialCommandHandler {

    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;
    private final String baseUrl;

    public UploadMaterialCommandHandler(
            CourseRepository courseRepository,
            FileStorageService fileStorageService,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.courseRepository = courseRepository;
        this.fileStorageService = fileStorageService;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public Long handle(UploadMaterialCommand command) throws FileStorageException, IOException {
        Course course = courseRepository.findByIdOrThrow(command.courseId());

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new IllegalArgumentException("You don't have permission to upload materials to this course");
        }

        Section section = course.getSections().stream()
                .filter(s -> s.getId().equals(command.sectionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));

        Lesson lesson = section.getLessons().stream()
                .filter(l -> l.getId().equals(command.lessonId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        String filename = fileStorageService.storeFile(command.file());
        String fileUrl = baseUrl + "/api/files/" + filename;

        MaterialType materialType = determineMaterialType(command.file().getContentType());

        Material material = new Material(command.title(), fileUrl, materialType);
        lesson.addMaterial(material);

        courseRepository.save(course);

        return material.getId();
    }

    private MaterialType determineMaterialType(String contentType) {
        if (contentType == null) {
            return MaterialType.DOCUMENT;
        }

        if (contentType.toLowerCase().startsWith("image/")) {
            return MaterialType.IMAGE;
        } else if (contentType.toLowerCase().equals("application/pdf")) {
            return MaterialType.PDF;
        } else {
            return MaterialType.DOCUMENT;
        }
    }
}
