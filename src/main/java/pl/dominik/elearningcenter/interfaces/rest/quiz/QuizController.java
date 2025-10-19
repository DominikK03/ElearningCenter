package pl.dominik.elearningcenter.interfaces.rest.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.quiz.command.*;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.input.*;
import pl.dominik.elearningcenter.application.quiz.query.*;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.quiz.request.*;
import pl.dominik.elearningcenter.interfaces.rest.quiz.response.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    // Command Use Cases
    private final CreateQuizUseCase createQuizUseCase;
    private final UpdateQuizUseCase updateQuizUseCase;
    private final DeleteQuizUseCase deleteQuizUseCase;
    private final AddQuestionUseCase addQuestionUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;
    private final SubmitQuizAttemptUseCase submitQuizAttemptUseCase;

    // Query Use Cases
    private final GetQuizDetailsUseCase getQuizDetailsUseCase;
    private final GetQuizForStudentUseCase getQuizForStudentUseCase;
    private final GetQuizAttemptResultUseCase getQuizAttemptResultUseCase;
    private final GetStudentQuizAttemptsUseCase getStudentQuizAttemptsUseCase;
    private final GetBestQuizAttemptUseCase getBestQuizAttemptUseCase;

    public QuizController(
            CreateQuizUseCase createQuizUseCase,
            UpdateQuizUseCase updateQuizUseCase,
            DeleteQuizUseCase deleteQuizUseCase,
            AddQuestionUseCase addQuestionUseCase,
            UpdateQuestionUseCase updateQuestionUseCase,
            DeleteQuestionUseCase deleteQuestionUseCase,
            SubmitQuizAttemptUseCase submitQuizAttemptUseCase,
            GetQuizDetailsUseCase getQuizDetailsUseCase,
            GetQuizForStudentUseCase getQuizForStudentUseCase,
            GetQuizAttemptResultUseCase getQuizAttemptResultUseCase,
            GetStudentQuizAttemptsUseCase getStudentQuizAttemptsUseCase,
            GetBestQuizAttemptUseCase getBestQuizAttemptUseCase
    ) {
        this.createQuizUseCase = createQuizUseCase;
        this.updateQuizUseCase = updateQuizUseCase;
        this.deleteQuizUseCase = deleteQuizUseCase;
        this.addQuestionUseCase = addQuestionUseCase;
        this.updateQuestionUseCase = updateQuestionUseCase;
        this.deleteQuestionUseCase = deleteQuestionUseCase;
        this.submitQuizAttemptUseCase = submitQuizAttemptUseCase;
        this.getQuizDetailsUseCase = getQuizDetailsUseCase;
        this.getQuizForStudentUseCase = getQuizForStudentUseCase;
        this.getQuizAttemptResultUseCase = getQuizAttemptResultUseCase;
        this.getStudentQuizAttemptsUseCase = getStudentQuizAttemptsUseCase;
        this.getBestQuizAttemptUseCase = getBestQuizAttemptUseCase;
    }

    @PostMapping
    public ResponseEntity<AckResponse> createQuiz(
            @RequestBody CreateQuizRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CreateQuizInput command = new CreateQuizInput(
                request.title(),
                request.passingScore(),
                request.lessonId(),
                userDetails.getUserId()
        );
        Long quizId = createQuizUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(quizId, "Quiz"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuizDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetQuizDetailsInput command = new GetQuizDetailsInput(id, userDetails.getUserId());
        QuizDTO dto = getQuizDetailsUseCase.execute(command);
        return ResponseEntity.ok(QuizResponse.from(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AckResponse> updateQuiz(
            @PathVariable Long id,
            @RequestBody UpdateQuizRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateQuizInput command = new UpdateQuizInput(
                id,
                request.title(),
                request.passingScore(),
                userDetails.getUserId()
        );
        updateQuizUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Quiz updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AckResponse> deleteQuiz(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteQuizInput command = new DeleteQuizInput(id, userDetails.getUserId());
        deleteQuizUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Quiz deleted successfully"));
    }


    @PostMapping("/{quizId}/questions")
    public ResponseEntity<AckResponse> addQuestion(
            @PathVariable Long quizId,
            @RequestBody AddQuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddQuestionInput command = new AddQuestionInput(
                quizId,
                request.text(),
                request.type(),
                request.orderIndex(),
                request.answers().stream()
                        .map(a -> new AddQuestionInput.AnswerInput(a.text(), a.correct()))
                        .toList(),
                userDetails.getUserId()
        );
        Long questionId = addQuestionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(questionId, "Question"));
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<AckResponse> updateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateQuestionInput command = new UpdateQuestionInput(
                quizId,
                questionId,
                request.text(),
                request.orderIndex(),
                request.points(),
                request.answers().stream()
                        .map(a -> new AddQuestionInput.AnswerInput(a.text(), a.correct()))
                        .toList(),
                userDetails.getUserId()
        );
        updateQuestionUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Question updated successfully"));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<AckResponse> deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteQuestionInput command = new DeleteQuestionInput(quizId, questionId, userDetails.getUserId());
        deleteQuestionUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Question deleted successfully"));
    }


    @GetMapping("/{id}/take")
    public ResponseEntity<QuizResponse> getQuizForStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        GetQuizForStudentInput command = new GetQuizForStudentInput(id, currentUser.getUserId());
        QuizDTO dto = getQuizForStudentUseCase.execute(command);
        return ResponseEntity.ok(QuizResponse.from(dto));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizAttemptResponse> submitQuizAttempt(
            @PathVariable Long id,
            @RequestBody SubmitQuizAttemptRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        SubmitQuizAttemptInput command = new SubmitQuizAttemptInput(
                id,
                userDetails.getUserId(),
                request.answers().stream()
                        .map(a -> new SubmitQuizAttemptInput.StudentAnswerInput(
                                a.questionId(),
                                a.selectedAnswerIndexes()
                        ))
                        .toList()
        );
        QuizAttemptDTO dto = submitQuizAttemptUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QuizAttemptResponse.from(dto));
    }


    @GetMapping("/{id}/attempts")
    public ResponseEntity<QuizAttemptsListResponse> getStudentQuizAttempts(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetStudentQuizAttemptsInput command = new GetStudentQuizAttemptsInput(id, userDetails.getUserId());
        List<QuizAttemptDTO> dtos = getStudentQuizAttemptsUseCase.execute(command);
        return ResponseEntity.ok(QuizAttemptsListResponse.from(dtos));
    }

    @GetMapping("/{id}/attempts/best")
    public ResponseEntity<QuizAttemptResponse> getBestAttempt(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetBestQuizAttemptInput command = new GetBestQuizAttemptInput(id, userDetails.getUserId());
        QuizAttemptDTO dto = getBestQuizAttemptUseCase.execute(command);

        if (dto == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(QuizAttemptResponse.from(dto));
    }

    @GetMapping("/{id}/attempts/{attemptId}")
    public ResponseEntity<QuizAttemptResponse> getAttemptResult(
            @PathVariable Long id,
            @PathVariable Long attemptId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GetQuizAttemptResultInput command = new GetQuizAttemptResultInput(attemptId, userDetails.getUserId());
        QuizAttemptDTO dto = getQuizAttemptResultUseCase.execute(command);
        return ResponseEntity.ok(QuizAttemptResponse.from(dto));
    }
}
