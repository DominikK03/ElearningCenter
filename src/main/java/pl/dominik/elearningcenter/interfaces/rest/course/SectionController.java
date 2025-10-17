package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.command.AddSectionUseCase;
import pl.dominik.elearningcenter.application.course.command.DeleteSectionUseCase;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionUseCase;
import pl.dominik.elearningcenter.application.course.input.AddSectionInput;
import pl.dominik.elearningcenter.application.course.input.DeleteSectionInput;
import pl.dominik.elearningcenter.application.course.input.UpdateSectionInput;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.AddSectionRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateSectionRequest;

@RestController
@RequestMapping("/api/courses/{courseId}/sections")
public class SectionController {
    private final AddSectionUseCase addSectionUseCase;
    private final UpdateSectionUseCase updateSectionUseCase;
    private final DeleteSectionUseCase deleteSectionUseCase;

    public SectionController(
            AddSectionUseCase addSectionUseCase,
            UpdateSectionUseCase updateSectionUseCase,
            DeleteSectionUseCase deleteSectionUseCase
    ) {
        this.addSectionUseCase = addSectionUseCase;
        this.updateSectionUseCase = updateSectionUseCase;
        this.deleteSectionUseCase = deleteSectionUseCase;
    }

    @PostMapping
    public ResponseEntity<AckResponse> addSection(
            @PathVariable Long courseId,
            @RequestBody AddSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddSectionInput command = new AddSectionInput(
                courseId,
                request.title(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        Long sectionId = addSectionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(sectionId, "Section"));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<AckResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody UpdateSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateSectionInput command = new UpdateSectionInput(
                courseId,
                sectionId,
                request.title(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        updateSectionUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.updated("Section"));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<AckResponse> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteSectionInput command = new DeleteSectionInput(
                courseId,
                sectionId,
                userDetails.getUserId()
        );

        deleteSectionUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Section deleted successfully"));
    }
}
