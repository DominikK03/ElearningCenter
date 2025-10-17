package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.command.AddLessonUseCase;
import pl.dominik.elearningcenter.application.course.command.DeleteLessonUseCase;
import pl.dominik.elearningcenter.application.course.command.UpdateLessonUseCase;
import pl.dominik.elearningcenter.application.course.input.AddLessonInput;
import pl.dominik.elearningcenter.application.course.input.DeleteLessonInput;
import pl.dominik.elearningcenter.application.course.input.UpdateLessonInput;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.AddLessonRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateLessonRequest;

@RestController
@RequestMapping("/api/courses/{courseId}/sections/{sectionId}/lessons")
public class LessonController {
    private final AddLessonUseCase addLessonUseCase;
    private final UpdateLessonUseCase updateLessonUseCase;
    private final DeleteLessonUseCase deleteLessonUseCase;

    public LessonController(
            AddLessonUseCase addLessonUseCase,
            UpdateLessonUseCase updateLessonUseCase,
            DeleteLessonUseCase deleteLessonUseCase
    ) {
        this.addLessonUseCase = addLessonUseCase;
        this.updateLessonUseCase = updateLessonUseCase;
        this.deleteLessonUseCase = deleteLessonUseCase;
    }

    @PostMapping
    public ResponseEntity<AckResponse> addLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody AddLessonRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddLessonInput command = new AddLessonInput(
                courseId,
                sectionId,
                request.title(),
                request.content(),
                request.orderIndex(),
                userDetails.getUserId()
        );
        Long lessonId = addLessonUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(lessonId, "Lesson"));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<AckResponse> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestBody UpdateLessonRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateLessonInput command = new UpdateLessonInput(
                courseId,
                sectionId,
                lessonId,
                request.title(),
                request.content(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        updateLessonUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.updated("Lesson"));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<AckResponse> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteLessonInput command = new DeleteLessonInput(
                courseId,
                sectionId,
                lessonId,
                userDetails.getUserId()
        );
        deleteLessonUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Lesson deleted successfully"));
    }
}
