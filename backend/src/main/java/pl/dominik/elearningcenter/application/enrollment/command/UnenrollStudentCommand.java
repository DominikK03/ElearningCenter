package pl.dominik.elearningcenter.application.enrollment.command;

public record UnenrollStudentCommand(Long enrollmentId, Long studentId) {
    public UnenrollStudentCommand {
        if (enrollmentId == null) throw new
                IllegalArgumentException("Enrollment ID cannot be null");
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
    }
}