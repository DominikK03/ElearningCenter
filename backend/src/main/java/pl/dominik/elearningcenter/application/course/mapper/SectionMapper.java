package pl.dominik.elearningcenter.application.course.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
import pl.dominik.elearningcenter.application.course.dto.SectionDTO;
import pl.dominik.elearningcenter.domain.course.Section;

import java.util.List;

@Component
public class SectionMapper {

    private final LessonMapper lessonMapper;

    public SectionMapper(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    public SectionDTO toDto(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        // Set quizId if quiz relationship exists
        if (section.getQuiz() != null) {
            section.setQuizId(section.getQuiz().getId());
        }

        List<LessonDTO> lessons = section.getLessons().stream()
                .map(lessonMapper::toDto)
                .toList();

        return new SectionDTO(
                section.getId(),
                section.getTitle(),
                section.getOrderIndex(),
                lessons,
                section.getQuizId()
        );
    }

    public SectionDTO toDtoSummary(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        // Set quizId if quiz relationship exists
        if (section.getQuiz() != null) {
            section.setQuizId(section.getQuiz().getId());
        }

        List<LessonDTO> lessons = section.getLessons().stream()
                .map(lessonMapper::toDtoSummary)  // Use summary mapping
                .toList();

        return new SectionDTO(
                section.getId(),
                section.getTitle(),
                section.getOrderIndex(),
                lessons,
                section.getQuizId()
        );
    }

    public SectionDTO toDtoMinimal(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        // Set quizId if quiz relationship exists
        if (section.getQuiz() != null) {
            section.setQuizId(section.getQuiz().getId());
        }

        return new SectionDTO(
                section.getId(),
                section.getTitle(),
                section.getOrderIndex(),
                List.of(),
                section.getQuizId()
        );
    }
}
