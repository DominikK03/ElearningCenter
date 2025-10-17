package pl.dominik.elearningcenter.application.course.input;

public record GetCoursesByInstructorInput(Long instructorId, int page, int size) {
    public GetCoursesByInstructorInput {
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Size must be 1-100");
    }
}