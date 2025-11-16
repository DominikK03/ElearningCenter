package pl.dominik.elearningcenter.interfaces.rest.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.quiz.command.AddQuestionCommand;
import pl.dominik.elearningcenter.application.quiz.command.AddQuestionCommandHandler;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuestionCommand;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuestionCommandHandler;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuestionCommand;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuestionCommandHandler;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.quiz.request.AddQuestionRequest;
import pl.dominik.elearningcenter.interfaces.rest.quiz.request.UpdateQuestionRequest;

/**
 * REST Controller for Quiz Question Management (instructor-only).
 * Handles CRUD operations for questions within a quiz.
 */
@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
public class QuizQuestionController {

    private final AddQuestionCommandHandler addQuestionHandler;
    private final UpdateQuestionCommandHandler updateQuestionHandler;
    private final DeleteQuestionCommandHandler deleteQuestionHandler;

    public QuizQuestionController(
            AddQuestionCommandHandler addQuestionHandler,
            UpdateQuestionCommandHandler updateQuestionHandler,
            DeleteQuestionCommandHandler deleteQuestionHandler
    ) {
        this.addQuestionHandler = addQuestionHandler;
        this.updateQuestionHandler = updateQuestionHandler;
        this.deleteQuestionHandler = deleteQuestionHandler;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> addQuestion(
            @PathVariable Long quizId,
            @RequestBody AddQuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddQuestionCommand command = new AddQuestionCommand(
                quizId,
                request.text(),
                request.type(),
                request.points(),
                request.orderIndex(),
                request.answers().stream()
                        .map(a -> new AddQuestionCommand.AnswerInput(a.text(), a.correct()))
                        .toList(),
                userDetails.getUserId()
        );
        Long questionId = addQuestionHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(questionId, "Question"));
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> updateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateQuestionCommand command = new UpdateQuestionCommand(
                quizId,
                questionId,
                request.text(),
                request.orderIndex(),
                request.points(),
                request.answers().stream()
                        .map(a -> new AddQuestionCommand.AnswerInput(a.text(), a.correct()))
                        .toList(),
                userDetails.getUserId()
        );
        updateQuestionHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Question updated successfully"));
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteQuestionCommand command = new DeleteQuestionCommand(quizId, questionId, userDetails.getUserId());
        deleteQuestionHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Question deleted successfully"));
    }
}
