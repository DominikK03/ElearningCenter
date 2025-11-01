package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.course.*;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class AddLinkMaterialCommandHandler {

    private final CourseRepository courseRepository;

    public AddLinkMaterialCommandHandler(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long handle(AddLinkMaterialCommand command) {
        if (!isValidUrl(command.url())) {
            throw new IllegalArgumentException("Invalid URL format");
        }

        Course course = courseRepository.findByIdOrThrow(command.courseId());

        if (!course.getInstructorId().equals(command.instructorId())) {
            throw new IllegalArgumentException("You don't have permission to add materials to this course");
        }

        Section section = course.getSections().stream()
                .filter(s -> s.getId().equals(command.sectionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Section not found"));

        Lesson lesson = section.getLessons().stream()
                .filter(l -> l.getId().equals(command.lessonId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Material material = new Material(command.title(), command.url(), MaterialType.LINK);
        lesson.addMaterial(material);

        // Save
        courseRepository.save(course);

        return material.getId();
    }

    private boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }

        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
