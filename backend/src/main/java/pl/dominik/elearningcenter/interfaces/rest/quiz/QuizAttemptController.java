package pl.dominik.elearningcenter.interfaces.rest.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.quiz.command.SubmitQuizAttemptCommand;
import pl.dominik.elearningcenter.application.quiz.command.SubmitQuizAttemptCommandHandler;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.query.GetBestQuizAttemptQuery;
import pl.dominik.elearningcenter.application.quiz.query.GetBestQuizAttemptQueryHandler;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizAttemptResultQuery;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizAttemptResultQueryHandler;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizForStudentQuery;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizForStudentQueryHandler;
import pl.dominik.elearningcenter.application.quiz.query.GetStudentQuizAttemptsQuery;
import pl.dominik.elearningcenter.application.quiz.query.GetStudentQuizAttemptsQueryHandler;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.quiz.request.SubmitQuizAttemptRequest;
import pl.dominik.elearningcenter.interfaces.rest.quiz.response.QuizAttemptResponse;
import pl.dominik.elearningcenter.interfaces.rest.quiz.response.QuizAttemptsListResponse;
import pl.dominik.elearningcenter.interfaces.rest.quiz.response.QuizResponse;

import java.util.List;

/**
 * REST Controller for Quiz Attempt operations (student-facing).
 * Handles quiz taking, submission, and viewing results.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizAttemptController {

    private final GetQuizForStudentQueryHandler getQuizForStudentHandler;
    private final SubmitQuizAttemptCommandHandler submitQuizAttemptHandler;
    private final GetStudentQuizAttemptsQueryHandler getStudentQuizAttemptsHandler;
    private final GetBestQuizAttemptQueryHandler getBestQuizAttemptHandler;
    private final GetQuizAttemptResultQueryHandler getQuizAttemptResultHandler;

    public QuizAttemptController(
            GetQuizForStudentQueryHandler getQuizForStudentHandler,
            SubmitQuizAttemptCommandHandler submitQuizAttemptHandler,
            GetStudentQuizAttemptsQueryHandler getStudentQuizAttemptsHandler,
            GetBestQuizAttemptQueryHandler getBestQuizAttemptHandler,
            GetQuizAttemptResultQueryHandler getQuizAttemptResultHandler
    ) {
        this.getQuizForStudentHandler = getQuizForStudentHandler;
        this.submitQuizAttemptHandler = submitQuizAttemptHandler;
        this.getStudentQuizAttemptsHandler = getStudentQuizAttemptsHandler;
        this.getBestQuizAttemptHandler = getBestQuizAttemptHandler;
        this.getQuizAttemptResultHandler = getQuizAttemptResultHandler;
    }

    @GetMapping("/{id}/take")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizResponse> getQuizForStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        GetQuizForStudentQuery query = new GetQuizForStudentQuery(id, currentUser.getUserId());
        QuizDTO dto = getQuizForStudentHandler.handle(query);
        return ResponseEntity.ok(QuizResponse.from(dto));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAttemptResponse> submitQuizAttempt(
            @PathVariable Long id,
            @RequestBody SubmitQuizAttemptRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        SubmitQuizAttemptCommand command = new SubmitQuizAttemptCommand(
                id,
                userDetails.getUserId(),
                request.answers().stream()
                        .map(a -> new SubmitQuizAttemptCommand.StudentAnswerInput(
                                a.questionId(),
                                a.selectedAnswerIndexes()
                        ))
                        .toList()
        );
        QuizAttemptDTO dto = submitQuizAttemptHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QuizAttemptResponse.from(dto));
    }

    @GetMapping("/{id}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAttemptsListResponse> getStudentQuizAttempts(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetStudentQuizAttemptsQuery query = new GetStudentQuizAttemptsQuery(id, userDetails.getUserId());
        List<QuizAttemptDTO> dtos = getStudentQuizAttemptsHandler.handle(query);
        return ResponseEntity.ok(QuizAttemptsListResponse.from(dtos));
    }

    @GetMapping("/{id}/attempts/best")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAttemptResponse> getBestAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetBestQuizAttemptQuery query = new GetBestQuizAttemptQuery(id, userDetails.getUserId());
        return getBestQuizAttemptHandler.handle(query)
                .map(dto -> ResponseEntity.ok(QuizAttemptResponse.from(dto)))
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}/attempts/{attemptId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAttemptResponse> getAttemptResult(
            @PathVariable Long id,
            @PathVariable Long attemptId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetQuizAttemptResultQuery query = new GetQuizAttemptResultQuery(attemptId, userDetails.getUserId());
        QuizAttemptDTO dto = getQuizAttemptResultHandler.handle(query);
        return ResponseEntity.ok(QuizAttemptResponse.from(dto));
    }
}
