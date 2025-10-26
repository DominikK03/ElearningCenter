package pl.dominik.elearningcenter.application.enrollment.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLesson;
import pl.dominik.elearningcenter.domain.enrollment.CompletedLessonRepository;
import pl.dominik.elearningcenter.domain.enrollment.Enrollment;
import pl.dominik.elearningcenter.domain.enrollment.EnrollmentRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

@Service
public class MarkLessonAsCompletedCommandHandler {
    private final EnrollmentRepository enrollmentRepository;
    private final CompletedLessonRepository completedLessonRepository;
    private final CourseRepository courseRepository;

    public MarkLessonAsCompletedCommandHandler(
            EnrollmentRepository enrollmentRepository,
            CompletedLessonRepository completedLessonRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.completedLessonRepository = completedLessonRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void handle(MarkLessonAsCompletedCommand command) {
        Enrollment enrollment = enrollmentRepository.findByIdOrThrow(command.enrollmentId());

        if (!enrollment.belongsToStudent(command.studentId())) {
            throw new DomainException("You can only mark lessons as completed for your own enrollments");
        }

        Course course = courseRepository.findByIdOrThrow(enrollment.getCourseId());
        course.findSection(command.sectionId()).findLesson(command.lessonId());

        if (completedLessonRepository.existsByEnrollmentIdAndLessonId(command.enrollmentId(), command.lessonId())) {
            return;
        }

        CompletedLesson completedLesson = CompletedLesson.create(command.enrollmentId(), command.lessonId());
        completedLessonRepository.save(completedLesson);

        int totalLessons = course.getTotalLessonsCount();
        long completedCount = completedLessonRepository.countByEnrollmentId(command.enrollmentId());
        int newProgress = (int) ((completedCount * 100) / totalLessons);

        enrollment.recalculateProgress(newProgress);
    }
}
