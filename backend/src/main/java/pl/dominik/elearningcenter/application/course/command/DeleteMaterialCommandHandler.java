package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.*;
import pl.dominik.elearningcenter.infrastructure.storage.FileStorageService;

import java.io.IOException;

@Service
public class DeleteMaterialCommandHandler {

    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    public DeleteMaterialCommandHandler(
            CourseRepository courseRepository,
            FileStorageService fileStorageService
    ) {
        this.courseRepository = courseRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public void handle(DeleteMaterialCommand command) {
        Course course = courseRepository.findByIdOrThrow(command.courseId());

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new IllegalArgumentException("You don't have permission to delete materials from this course");
        }

        Section section = course.getSections().stream()
                .filter(s -> s.getId().equals(command.sectionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));

        Lesson lesson = section.getLessons().stream()
                .filter(l -> l.getId().equals(command.lessonId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Material material = lesson.getMaterials().stream()
                .filter(m -> m.getId().equals(command.materialId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));

        String fileUrl = material.getFileUrl();
        String filename = extractFilenameFromUrl(fileUrl);

        lesson.removeMaterial(command.materialId());

        courseRepository.save(course);

        try {
            fileStorageService.deleteFile(filename);
        } catch (IOException e) {
            System.err.println("Failed to delete file from storage: " + filename);
        }
    }

    private String extractFilenameFromUrl(String fileUrl) {
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < fileUrl.length() - 1) {
            return fileUrl.substring(lastSlashIndex + 1);
        }
        return fileUrl;
    }
}
