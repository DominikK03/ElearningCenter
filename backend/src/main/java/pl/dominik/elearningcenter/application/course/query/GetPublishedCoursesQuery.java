package pl.dominik.elearningcenter.application.course.query;

public record GetPublishedCoursesQuery(int page, int size) {
    public GetPublishedCoursesQuery {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Size must be 1-100");
    }
}
