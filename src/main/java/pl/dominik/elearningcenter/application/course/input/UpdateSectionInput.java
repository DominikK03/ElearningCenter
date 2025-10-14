package pl.dominik.elearningcenter.application.course.input;

public record UpdateSectionInput(
        Long courseId,
        Long sectionId,
        String title,
        Integer orderIndex,
        Long instructorId
) {
}
