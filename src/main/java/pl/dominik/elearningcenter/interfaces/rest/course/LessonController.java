package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.command.AddLessonCommandHandler;
import pl.dominik.elearningcenter.application.course.command.DeleteLessonCommandHandler;
import pl.dominik.elearningcenter.application.course.command.UpdateLessonCommandHandler;
import pl.dominik.elearningcenter.application.course.command.AddLessonCommand;
import pl.dominik.elearningcenter.application.course.command.DeleteLessonCommand;
import pl.dominik.elearningcenter.application.course.command.UpdateLessonCommand;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.AddLessonRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateLessonRequest;

@RestController
@RequestMapping("/api/courses/{courseId}/sections/{sectionId}/lessons")
public class LessonController {
    private final AddLessonCommandHandler addLessonCommandHandler;
    private final UpdateLessonCommandHandler updateLessonCommandHandler;
    private final DeleteLessonCommandHandler deleteLessonCommandHandler;

    public LessonController(
            AddLessonCommandHandler addLessonCommandHandler,
            UpdateLessonCommandHandler updateLessonCommandHandler,
            DeleteLessonCommandHandler deleteLessonCommandHandler
    ) {
        this.addLessonCommandHandler = addLessonCommandHandler;
        this.updateLessonCommandHandler = updateLessonCommandHandler;
        this.deleteLessonCommandHandler = deleteLessonCommandHandler;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> addLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody AddLessonRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddLessonCommand command = new AddLessonCommand(
                courseId,
                sectionId,
                request.title(),
                request.content(),
                request.orderIndex(),
                userDetails.getUserId()
        );
        Long lessonId = addLessonCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(lessonId, "Lesson"));
    }

    @PutMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestBody UpdateLessonRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateLessonCommand command = new UpdateLessonCommand(
                courseId,
                sectionId,
                lessonId,
                request.title(),
                request.content(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        updateLessonCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.updated("Lesson"));
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteLessonCommand command = new DeleteLessonCommand(
                courseId,
                sectionId,
                lessonId,
                userDetails.getUserId()
        );
        deleteLessonCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Lesson deleted successfully"));
    }
}
