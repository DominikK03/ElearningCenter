package pl.dominik.elearningcenter.application.course.input;

public record DeleteSectionInput(
        Long courseId,
        Long sectionId,
        Long instructorId
) {
}
