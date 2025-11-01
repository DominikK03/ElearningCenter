package pl.dominik.elearningcenter.interfaces.rest.enrollment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.enrollment.command.EnrollStudentCommand;
import pl.dominik.elearningcenter.application.enrollment.command.EnrollStudentCommandHandler;
import pl.dominik.elearningcenter.application.enrollment.command.MarkLessonAsCompletedCommand;
import pl.dominik.elearningcenter.application.enrollment.command.MarkLessonAsCompletedCommandHandler;
import pl.dominik.elearningcenter.application.enrollment.command.UnenrollStudentCommand;
import pl.dominik.elearningcenter.application.enrollment.command.UnenrollStudentCommandHandler;
import pl.dominik.elearningcenter.application.enrollment.query.GetCourseEnrollmentsQuery;
import pl.dominik.elearningcenter.application.enrollment.query.GetCourseEnrollmentsQueryHandler;
import pl.dominik.elearningcenter.application.enrollment.query.GetStudentEnrollmentsQuery;
import pl.dominik.elearningcenter.application.enrollment.query.GetStudentEnrollmentsQueryHandler;
import pl.dominik.elearningcenter.application.enrollment.query.GetCompletedLessonsQuery;
import pl.dominik.elearningcenter.application.enrollment.query.GetCompletedLessonsQueryHandler;
import pl.dominik.elearningcenter.application.enrollment.dto.EnrollmentDTO;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.enrollment.request.EnrollStudentRequest;
import pl.dominik.elearningcenter.interfaces.rest.enrollment.response.EnrollmentResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollStudentCommandHandler enrollStudentCommandHandler;
    private final GetStudentEnrollmentsQueryHandler getStudentEnrollmentsQueryHandler;
    private final GetCourseEnrollmentsQueryHandler getCourseEnrollmentsQueryHandler;
    private final UnenrollStudentCommandHandler unenrollStudentCommandHandler;
    private final MarkLessonAsCompletedCommandHandler markLessonAsCompletedCommandHandler;
    private final GetCompletedLessonsQueryHandler getCompletedLessonsQueryHandler;

    public EnrollmentController(
            EnrollStudentCommandHandler enrollStudentCommandHandler,
            GetStudentEnrollmentsQueryHandler getStudentEnrollmentsQueryHandler,
            GetCourseEnrollmentsQueryHandler getCourseEnrollmentsQueryHandler,
            UnenrollStudentCommandHandler unenrollStudentCommandHandler,
            MarkLessonAsCompletedCommandHandler markLessonAsCompletedCommandHandler,
            GetCompletedLessonsQueryHandler getCompletedLessonsQueryHandler
    ) {
        this.enrollStudentCommandHandler = enrollStudentCommandHandler;
        this.getStudentEnrollmentsQueryHandler = getStudentEnrollmentsQueryHandler;
        this.getCourseEnrollmentsQueryHandler = getCourseEnrollmentsQueryHandler;
        this.unenrollStudentCommandHandler = unenrollStudentCommandHandler;
        this.markLessonAsCompletedCommandHandler = markLessonAsCompletedCommandHandler;
        this.getCompletedLessonsQueryHandler = getCompletedLessonsQueryHandler;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollStudent(@RequestBody EnrollStudentRequest request) {
        EnrollStudentCommand command = new EnrollStudentCommand(request.studentId(), request.courseId());
        EnrollmentDTO enrollmentDTO = enrollStudentCommandHandler.handle(command);
        EnrollmentResponse response = EnrollmentResponse.from(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollments(@PathVariable Long studentId) {
        GetStudentEnrollmentsQuery query = new GetStudentEnrollmentsQuery(studentId);
        List<EnrollmentDTO> enrollmentDTOs = getStudentEnrollmentsQueryHandler.handle(query);
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
        UnenrollStudentCommand command = new UnenrollStudentCommand(id, currentUser.getUserId());
        unenrollStudentCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Unenrolled successfully"));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getCourseEnrollments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        GetCourseEnrollmentsQuery query = new GetCourseEnrollmentsQuery(courseId, currentUser.getUserId());
        List<EnrollmentDTO> enrollmentDTOs = getCourseEnrollmentsQueryHandler.handle(query);
        List<EnrollmentResponse> responses = enrollmentDTOs.stream()
                .map(EnrollmentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{enrollmentId}/sections/{sectionId}/lessons/{lessonId}/complete")
    public ResponseEntity<AckResponse> markLessonAsCompleted(
            @PathVariable Long enrollmentId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        MarkLessonAsCompletedCommand command = new MarkLessonAsCompletedCommand(
                enrollmentId,
                sectionId,
                lessonId,
                currentUser.getUserId()
        );
        markLessonAsCompletedCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Lesson marked as completed"));
    }

    @GetMapping("/{enrollmentId}/completed-lessons")
    public ResponseEntity<List<Long>> getCompletedLessons(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        GetCompletedLessonsQuery query = new GetCompletedLessonsQuery(enrollmentId, currentUser.getUserId());
        List<Long> completedLessonIds = getCompletedLessonsQueryHandler.handle(query);
        return ResponseEntity.ok(completedLessonIds);
    }
}
