package pl.dominik.elearningcenter.application.enrollment.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLesson;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLessonRepository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.enrollment.exception.EnrollmentAccessDeniedException;

import java.util.List;

@Service
public class GetCompletedLessonsQueryHandler {

    private final CompletedLessonRepository completedLessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    public GetCompletedLessonsQueryHandler(
            CompletedLessonRepository completedLessonRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.completedLessonRepository = completedLessonRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Long> handle(GetCompletedLessonsQuery query) {
        Enrollment enrollment = enrollmentRepository.findByIdOrThrow(query.enrollmentId());

        if (!enrollment.getStudentId().equals(query.userId())) {
            throw new EnrollmentAccessDeniedException("You don't have access to this enrollment");
        }

        return completedLessonRepository.findByEnrollmentId(query.enrollmentId())
                .stream()
                .map(CompletedLesson::getLessonId)
                .toList();
    }
}
