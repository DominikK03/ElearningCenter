package pl.dominik.elearningcenter.interfaces.rest.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.quiz.command.CreateQuizCommand;
import pl.dominik.elearningcenter.application.quiz.command.CreateQuizCommandHandler;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuizCommand;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuizCommandHandler;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuizCommand;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuizCommandHandler;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.query.GetCourseQuizzesQuery;
import pl.dominik.elearningcenter.application.quiz.query.GetCourseQuizzesQueryHandler;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizDetailsQuery;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizDetailsQueryHandler;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;

import java.util.List;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.quiz.request.CreateQuizRequest;
import pl.dominik.elearningcenter.interfaces.rest.quiz.request.UpdateQuizRequest;
import pl.dominik.elearningcenter.interfaces.rest.quiz.response.QuizResponse;

/**
 * REST Controller for Quiz Management operations (instructor-only).
 * Handles CRUD operations for quizzes.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizManagementController {

    private final CreateQuizCommandHandler createQuizHandler;
    private final UpdateQuizCommandHandler updateQuizHandler;
    private final DeleteQuizCommandHandler deleteQuizHandler;
    private final GetQuizDetailsQueryHandler getQuizDetailsHandler;
    private final GetCourseQuizzesQueryHandler getCourseQuizzesHandler;

    public QuizManagementController(
            CreateQuizCommandHandler createQuizHandler,
            UpdateQuizCommandHandler updateQuizHandler,
            DeleteQuizCommandHandler deleteQuizHandler,
            GetQuizDetailsQueryHandler getQuizDetailsHandler,
            GetCourseQuizzesQueryHandler getCourseQuizzesHandler
    ) {
        this.createQuizHandler = createQuizHandler;
        this.updateQuizHandler = updateQuizHandler;
        this.deleteQuizHandler = deleteQuizHandler;
        this.getQuizDetailsHandler = getQuizDetailsHandler;
        this.getCourseQuizzesHandler = getCourseQuizzesHandler;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> createQuiz(
            @RequestBody CreateQuizRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CreateQuizCommand command = new CreateQuizCommand(
                request.title(),
                request.passingScore(),
                request.courseId(),
                request.sectionId(),
                request.lessonId(),
                userDetails.getUserId()
        );
        Long quizId = createQuizHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(quizId, "Quiz"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<QuizResponse> getQuizDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetQuizDetailsQuery query = new GetQuizDetailsQuery(id, userDetails.getUserId());
        QuizDTO dto = getQuizDetailsHandler.handle(query);
        return ResponseEntity.ok(QuizResponse.from(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> updateQuiz(
            @PathVariable Long id,
            @RequestBody UpdateQuizRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateQuizCommand command = new UpdateQuizCommand(
                id,
                request.title(),
                request.passingScore(),
                userDetails.getUserId()
        );
        updateQuizHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Quiz updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> deleteQuiz(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteQuizCommand command = new DeleteQuizCommand(id, userDetails.getUserId());
        deleteQuizHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Quiz deleted successfully"));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<QuizResponse>> getQuizzesByCourse(
            @PathVariable Long courseId
    ) {
        GetCourseQuizzesQuery query = new GetCourseQuizzesQuery(courseId);
        List<QuizDTO> quizzes = getCourseQuizzesHandler.handle(query);
        List<QuizResponse> responses = quizzes.stream()
                .map(QuizResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
