package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.command.AddSectionCommandHandler;
import pl.dominik.elearningcenter.application.course.command.DeleteSectionCommandHandler;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionCommandHandler;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionsOrderCommandHandler;
import pl.dominik.elearningcenter.application.course.command.AddSectionCommand;
import pl.dominik.elearningcenter.application.course.command.DeleteSectionCommand;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionCommand;
import pl.dominik.elearningcenter.application.course.command.UpdateSectionsOrderCommand;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.AddSectionRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateSectionRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateSectionsOrderRequest;

@RestController
@RequestMapping("/api/courses/{courseId}/sections")
public class SectionController {
    private final AddSectionCommandHandler addSectionCommandHandler;
    private final UpdateSectionCommandHandler updateSectionCommandHandler;
    private final DeleteSectionCommandHandler deleteSectionCommandHandler;
    private final UpdateSectionsOrderCommandHandler updateSectionsOrderCommandHandler;

    public SectionController(
            AddSectionCommandHandler addSectionCommandHandler,
            UpdateSectionCommandHandler updateSectionCommandHandler,
            DeleteSectionCommandHandler deleteSectionCommandHandler,
            UpdateSectionsOrderCommandHandler updateSectionsOrderCommandHandler
    ) {
        this.addSectionCommandHandler = addSectionCommandHandler;
        this.updateSectionCommandHandler = updateSectionCommandHandler;
        this.deleteSectionCommandHandler = deleteSectionCommandHandler;
        this.updateSectionsOrderCommandHandler = updateSectionsOrderCommandHandler;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> addSection(
            @PathVariable Long courseId,
            @RequestBody AddSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddSectionCommand command = new AddSectionCommand(
                courseId,
                request.title(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        Long sectionId = addSectionCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(sectionId, "Section"));
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody UpdateSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateSectionCommand command = new UpdateSectionCommand(
                courseId,
                sectionId,
                request.title(),
                userDetails.getUserId()
        );

        updateSectionCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.updated("Section"));
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteSectionCommand command = new DeleteSectionCommand(
                courseId,
                sectionId,
                userDetails.getUserId()
        );

        deleteSectionCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Section deleted successfully"));
    }

    @PatchMapping("/order")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> updateSectionsOrder(
            @PathVariable Long courseId,
            @RequestBody UpdateSectionsOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateSectionsOrderCommand command = new UpdateSectionsOrderCommand(
                courseId,
                request.sectionOrderMap(),
                userDetails.getUserId()
        );

        updateSectionsOrderCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Sections order updated successfully"));
    }
}
