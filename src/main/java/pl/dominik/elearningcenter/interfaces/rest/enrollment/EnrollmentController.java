package pl.dominik.elearningcenter.interfaces.rest.enrollment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.enrollment.command.EnrollStudentUseCase;
import pl.dominik.elearningcenter.application.enrollment.command.UnenrollStudentUseCase;
import pl.dominik.elearningcenter.application.enrollment.command.UpdateProgressUseCase;
import pl.dominik.elearningcenter.application.enrollment.query.GetStudentEnrollmentsUseCase;
import pl.dominik.elearningcenter.application.enrollment.query.GetCourseEnrollmentsUseCase;
import pl.dominik.elearningcenter.application.enrollment.input.EnrollStudentInput;
import pl.dominik.elearningcenter.application.enrollment.input.GetCourseEnrollmentsInput;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.input.UnenrollStudentInput;
import pl.dominik.elearningcenter.application.enrollment.input.UpdateProgressInput;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.enrollment.request.EnrollStudentRequest;
import pl.dominik.elearningcenter.interfaces.rest.enrollment.response.EnrollmentResponse;
import pl.dominik.elearningcenter.interfaces.rest.enrollment.request.UpdateProgressRequest;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollStudentUseCase enrollStudentUseCase;
    private final UpdateProgressUseCase updateProgressUseCase;
    private final GetStudentEnrollmentsUseCase getStudentEnrollmentsUseCase;
    private final GetCourseEnrollmentsUseCase getCourseEnrollmentsUseCase;
    private final UnenrollStudentUseCase unenrollStudentUseCase;

    public EnrollmentController(
            EnrollStudentUseCase enrollStudentUseCase,
            UpdateProgressUseCase updateProgressUseCase,
            GetStudentEnrollmentsUseCase getStudentEnrollmentsUseCase,
            GetCourseEnrollmentsUseCase getCourseEnrollmentsUseCase,
            UnenrollStudentUseCase unenrollStudentUseCase
    ) {
        this.enrollStudentUseCase = enrollStudentUseCase;
        this.updateProgressUseCase = updateProgressUseCase;
        this.getStudentEnrollmentsUseCase = getStudentEnrollmentsUseCase;
        this.getCourseEnrollmentsUseCase = getCourseEnrollmentsUseCase;
        this.unenrollStudentUseCase = unenrollStudentUseCase;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollStudent(@RequestBody EnrollStudentRequest request){
        EnrollStudentInput command = new EnrollStudentInput(request.studentId(), request.courseId());
        EnrollmentDTO enrollmentDTO = enrollStudentUseCase.execute(command);
        EnrollmentResponse response = EnrollmentResponse.from(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<EnrollmentResponse> updateProgress(@PathVariable Long id, @RequestBody UpdateProgressRequest request){
        UpdateProgressInput command = new UpdateProgressInput(id, request.percentage());
        EnrollmentDTO enrollmentDTO = updateProgressUseCase.execute(command);
        EnrollmentResponse response = EnrollmentResponse.from(enrollmentDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollments(@PathVariable Long studentId){
        List<EnrollmentDTO> enrollmentDTOs = getStudentEnrollmentsUseCase.execute(studentId);
        List<EnrollmentResponse> responses = enrollmentDTOs.stream()
                .map(EnrollmentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AckResponse> unenrollStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UnenrollStudentInput command = new UnenrollStudentInput(id, currentUser.getUserId());
        unenrollStudentUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Unenrolled successfully"));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getCourseEnrollments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        GetCourseEnrollmentsInput command = new GetCourseEnrollmentsInput(courseId, currentUser.getUserId());
        List<EnrollmentDTO> enrollmentDTOs = getCourseEnrollmentsUseCase.execute(command);
        List<EnrollmentResponse> responses = enrollmentDTOs.stream()
                .map(EnrollmentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
