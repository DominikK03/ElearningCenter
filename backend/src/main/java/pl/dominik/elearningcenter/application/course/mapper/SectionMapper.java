package pl.dominik.elearningcenter.application.course.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
import pl.dominik.elearningcenter.application.course.dto.SectionDTO;
import pl.dominik.elearningcenter.domain.course.Section;

import java.util.List;

@Component
public class SectionMapper {

    private static final Logger log = LoggerFactory.getLogger(SectionMapper.class);
    private final LessonMapper lessonMapper;

    public SectionMapper(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    public SectionDTO toDto(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        log.debug("Mapping section to DTO: id={}, title={}", section.getId(), section.getTitle());

        // Set quizId if quiz relationship exists
        try {
            if (section.getQuiz() != null) {
                log.debug("Section {} has quiz with id={}", section.getId(), section.getQuiz().getId());
                section.setQuizId(section.getQuiz().getId());
            } else {
                log.debug("Section {} has no quiz", section.getId());
            }
        } catch (Exception e) {
            log.error("Error accessing quiz for section {}: {}", section.getId(), e.getMessage(), e);
            throw e;
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

        log.debug("Mapping section to DTO summary: id={}, title={}", section.getId(), section.getTitle());

        // Set quizId if quiz relationship exists
        try {
            if (section.getQuiz() != null) {
                log.debug("Section {} has quiz with id={}", section.getId(), section.getQuiz().getId());
                section.setQuizId(section.getQuiz().getId());
            } else {
                log.debug("Section {} has no quiz", section.getId());
            }
        } catch (Exception e) {
            log.error("Error accessing quiz for section {} in summary: {}", section.getId(), e.getMessage(), e);
            throw e;
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
