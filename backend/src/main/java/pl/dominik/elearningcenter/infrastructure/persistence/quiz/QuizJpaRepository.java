package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dominik.elearningcenter.domain.quiz.Quiz;

import java.util.List;
import java.util.Optional;

interface QuizJpaRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByLesson_Id(Long lessonId);

    Optional<Quiz> findByIdAndInstructorId(Long id, Long instructorId);

    List<Quiz> findByInstructorId(Long instructorId);

    @Query("""
        SELECT DISTINCT q FROM Quiz q
        LEFT JOIN FETCH q.questions
        WHERE q.course.id = :courseId
           OR q.section.id IN (SELECT s.id FROM Section s WHERE s.course.id = :courseId)
           OR q.lesson.id IN (SELECT l.id FROM Lesson l WHERE l.section.course.id = :courseId)
        """)
    List<Quiz> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.section.id = :sectionId")
    List<Quiz> findBySectionId(@Param("sectionId") Long sectionId);

    boolean existsByLesson_Id(Long lessonId);

    boolean existsByCourse_IdAndSectionIsNullAndLessonIsNull(Long courseId);

    boolean existsBySection_IdAndLessonIsNull(Long sectionId);

    Optional<Quiz> findByCourse_IdAndSectionIsNullAndLessonIsNull(Long courseId);

    Optional<Quiz> findBySection_IdAndLessonIsNull(Long sectionId);
}
