package pl.dominik.elearningcenter.application.course.input;

public record AddSectionInput(
        Long courseId,
        String title,
        Integer orderIndex
) {

}
