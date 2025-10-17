package pl.dominik.elearningcenter.domain.enrollment;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.enrollment.valueobject.Progress;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class Enrollment extends AggregateRoot<Long> {

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Embedded
    private Progress progress;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    protected Enrollment() {
        super();
    }

    private Enrollment(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.progress = Progress.zero();
        this.enrolledAt = LocalDateTime.now();
        this.status = EnrollmentStatus.ACTIVE;
    }

    public static Enrollment enroll(Long studentId, Long courseId) {
        return new Enrollment(
                studentId,
                courseId
        );
    }

    public void recalculateProgress(int percentage) {
        this.progress = Progress.of(percentage);

        if (this.progress.isCompleted() && this.status == EnrollmentStatus.ACTIVE) {
            complete();
        }
    }

    public void complete() {
        this.status = EnrollmentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void drop() {
        if (this.status == EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot drop a completed enrollment");
        }
        this.status = EnrollmentStatus.DROPPED;

    }

    public boolean isActive() {
        return this.status == EnrollmentStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return this.status == EnrollmentStatus.COMPLETED;
    }

    public boolean belongsToStudent(Long studentId) {
        return this.studentId.equals(studentId);
    }

    public boolean isForCourse(Long courseId) {
        return this.courseId.equals(courseId);
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Progress getProgress() {
        return progress;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }


}
