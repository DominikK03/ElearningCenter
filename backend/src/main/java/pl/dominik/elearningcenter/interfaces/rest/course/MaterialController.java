package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dominik.elearningcenter.application.course.command.UploadMaterialCommand;
import pl.dominik.elearningcenter.application.course.command.UploadMaterialCommandHandler;
import pl.dominik.elearningcenter.application.course.command.DeleteMaterialCommand;
import pl.dominik.elearningcenter.application.course.command.DeleteMaterialCommandHandler;
import pl.dominik.elearningcenter.application.course.command.AddLinkMaterialCommand;
import pl.dominik.elearningcenter.application.course.command.AddLinkMaterialCommandHandler;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.infrastructure.storage.FileStorageException;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;

@RestController
@RequestMapping("/api/courses")
public class MaterialController {

    private final UploadMaterialCommandHandler uploadMaterialCommandHandler;
    private final DeleteMaterialCommandHandler deleteMaterialCommandHandler;
    private final AddLinkMaterialCommandHandler addLinkMaterialCommandHandler;

    public MaterialController(
            UploadMaterialCommandHandler uploadMaterialCommandHandler,
            DeleteMaterialCommandHandler deleteMaterialCommandHandler,
            AddLinkMaterialCommandHandler addLinkMaterialCommandHandler
    ) {
        this.uploadMaterialCommandHandler = uploadMaterialCommandHandler;
        this.deleteMaterialCommandHandler = deleteMaterialCommandHandler;
        this.addLinkMaterialCommandHandler = addLinkMaterialCommandHandler;
    }

    @PostMapping("/{courseId}/sections/{sectionId}/lessons/{lessonId}/materials")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> uploadMaterial(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            UploadMaterialCommand command = new UploadMaterialCommand(
                    courseId,
                    sectionId,
                    lessonId,
                    title,
                    file,
                    currentUser.getUserId()
            );

            Long materialId = uploadMaterialCommandHandler.handle(command);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AckResponse.of("Material uploaded successfully", materialId));

        } catch (FileStorageException e) {
            return ResponseEntity.badRequest()
                    .body(AckResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AckResponse.error(e.getMessage()));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AckResponse.error("File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AckResponse.error("Failed to upload material: " + e.getMessage()));
        }
    }

    @PostMapping("/{courseId}/sections/{sectionId}/lessons/{lessonId}/materials/link")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<AckResponse> addLinkMaterial(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestParam("title") String title,
            @RequestParam("url") String url,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            AddLinkMaterialCommand command = new AddLinkMaterialCommand(
                    courseId,
                    sectionId,
                    lessonId,
                    title,
                    url,
                    currentUser.getUserId()
            );

            Long materialId = addLinkMaterialCommandHandler.handle(command);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AckResponse.of("Link material added successfully", materialId));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AckResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AckResponse.error("Failed to add link material: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}/lessons/{lessonId}/materials/{materialId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<AckResponse> deleteMaterial(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @PathVariable Long materialId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        try {
            DeleteMaterialCommand command = new DeleteMaterialCommand(
                    courseId,
                    sectionId,
                    lessonId,
                    materialId,
                    currentUser.getUserId()
            );

            deleteMaterialCommandHandler.handle(command);

            return ResponseEntity.ok(AckResponse.success("Material deleted successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AckResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AckResponse.error("Failed to delete material: " + e.getMessage()));
        }
    }
}
