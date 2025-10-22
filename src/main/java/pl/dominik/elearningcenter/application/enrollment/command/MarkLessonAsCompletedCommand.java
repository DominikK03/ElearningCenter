package pl.dominik.elearningcenter.application.enrollment.command;

public record MarkLessonAsCompletedCommand(
        Long enrollmentId,
        Long sectionId,
        Long lessonId,
        Long studentId
) {
    public MarkLessonAsCompletedCommand {
        if (enrollmentId == null) throw new IllegalArgumentException("Enrollment ID cannot be null");
        if (sectionId == null) throw new IllegalArgumentException("Section ID cannot be null");
        if (lessonId == null) throw new IllegalArgumentException("Lesson ID cannot be null");
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
    }
}
