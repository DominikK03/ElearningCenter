package pl.dominik.elearningcenter.domain.enrollment;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "completed_lesson",
        uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"}))
public class CompletedLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    protected CompletedLesson() {}

    private CompletedLesson(
            Long enrollmentId,
            Long lessonId,
            LocalDateTime completedAt
    ) {
        this.enrollmentId =enrollmentId;
        this.lessonId = lessonId;
        this.completedAt = completedAt;
    }

    public static CompletedLesson create(Long enrollmentId, Long lessonId){
        return new CompletedLesson(enrollmentId, lessonId, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

}
