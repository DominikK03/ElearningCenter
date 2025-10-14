package pl.dominik.elearningcenter.interfaces.rest.enrollment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.enrollment.EnrollStudentUseCase;
import pl.dominik.elearningcenter.application.enrollment.GetStudentEnrollmentsUseCase;
import pl.dominik.elearningcenter.application.enrollment.UpdateProgressUseCase;
import pl.dominik.elearningcenter.application.enrollment.input.EnrollStudentInput;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.application.enrollment.input.UpdateProgressInput;
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

    public EnrollmentController(EnrollStudentUseCase enrollStudentUseCase, UpdateProgressUseCase updateProgressUseCase, GetStudentEnrollmentsUseCase getStudentEnrollmentsUseCase) {
        this.enrollStudentUseCase = enrollStudentUseCase;
        this.updateProgressUseCase = updateProgressUseCase;
        this.getStudentEnrollmentsUseCase = getStudentEnrollmentsUseCase;
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
}
