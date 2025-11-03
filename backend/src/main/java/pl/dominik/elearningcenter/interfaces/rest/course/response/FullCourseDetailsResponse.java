package pl.dominik.elearningcenter.interfaces.rest.course.response;

import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.SectionDTO;
import pl.dominik.elearningcenter.application.course.dto.LessonDTO;
import pl.dominik.elearningcenter.application.course.dto.MaterialDTO;
import pl.dominik.elearningcenter.domain.course.CourseLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FullCourseDetailsResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String currency,
        String thumbnailUrl,
        String category,
        CourseLevel level,
        Long instructorId,
        boolean published,
        LocalDateTime createdAt,
        List<SectionResponse> sections,
        int sectionsCount,
        int totalLessonsCount,
        Long quizId
) {
    public static FullCourseDetailsResponse from(CourseDTO dto) {
        var sections = dto.sections().stream()
                .map(SectionResponse::from)
                .collect(Collectors.toList());

        return new FullCourseDetailsResponse(
                dto.id(),
                dto.title(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.thumbnailUrl(),
                dto.category(),
                dto.level(),
                dto.instructorId(),
                dto.published(),
                dto.createdAt(),
                sections,
                dto.sectionsCount(),
                dto.totalLessonsCount(),
                dto.quizId()
        );
    }

    public record SectionResponse(
            Long id,
            String title,
            Integer orderIndex,
            List<LessonResponse> lessons,
            Long quizId
    ) {
        public static SectionResponse from(SectionDTO dto) {
            var lessons = dto.lessons().stream()
                    .map(LessonResponse::from)
                    .collect(Collectors.toList());

            return new SectionResponse(
                    dto.id(),
                    dto.title(),
                    dto.orderIndex(),
                    lessons,
                    dto.quizId()
            );
        }
    }

    public record LessonResponse(
            Long id,
            String title,
            String content,
            String videoUrl,
            Integer durationMinutes,
            Integer orderIndex,
            List<MaterialResponse> materials,
            Long quizId
    ) {
        public static LessonResponse from(LessonDTO dto) {
            var materials = dto.materials().stream()
                    .map(MaterialResponse::from)
                    .collect(Collectors.toList());

            return new LessonResponse(
                    dto.id(),
                    dto.title(),
                    dto.content(),
                    dto.videoUrl(),
                    dto.durationMinutes(),
                    dto.orderIndex(),
                    materials,
                    dto.quizId()
            );
        }
    }

    public record MaterialResponse(
            Long id,
            String title,
            String fileUrl,
            String fileType
    ) {
        public static MaterialResponse from(MaterialDTO dto) {
            return new MaterialResponse(
                    dto.id(),
                    dto.title(),
                    dto.fileUrl(),
                    dto.fileType().name()
            );
        }
    }
}
