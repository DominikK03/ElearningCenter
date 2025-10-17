package pl.dominik.elearningcenter.application.enrollment.input;

public record UnenrollStudentInput(Long enrollmentId, Long studentId) {
    public UnenrollStudentInput {
        if (enrollmentId == null) throw new
                IllegalArgumentException("Enrollment ID cannot be null");
        if (studentId == null) throw new IllegalArgumentException("Student ID cannot be null");
    }
}