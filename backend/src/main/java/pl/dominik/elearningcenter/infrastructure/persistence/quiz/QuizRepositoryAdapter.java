package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import org.springframework.stereotype.Repository;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizRepositoryAdapter implements QuizRepository {
    private final QuizJpaRepository jpaRepository;

    public QuizRepositoryAdapter(QuizJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    @Override
    public Quiz save(Quiz quiz) {
        return jpaRepository.save(quiz);
    }

    @Override
    public Optional<Quiz> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Quiz> findByIdAndInstructorId(Long id, Long instructorId) {
        return jpaRepository.findByIdAndInstructorId(id, instructorId);
    }

    @Override
    public List<Quiz> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Quiz> findByLessonId(Long lessonId) {
        return jpaRepository.findByLesson_Id(lessonId);
    }

    @Override
    public List<Quiz> findByInstructorId(Long instructorId) {
        return jpaRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<Quiz> findByCourseId(Long courseId) {
        return jpaRepository.findByCourseId(courseId);
    }

    @Override
    public List<Quiz> findBySectionId(Long sectionId) {
        return jpaRepository.findBySectionId(sectionId);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByLessonId(Long lessonId) {
        return jpaRepository.existsByLesson_Id(lessonId);
    }

    @Override
    public boolean existsByCourseIdOnly(Long courseId) {
        return jpaRepository.existsByCourse_IdAndSectionIsNullAndLessonIsNull(courseId);
    }

    @Override
    public boolean existsBySectionIdOnly(Long sectionId) {
        return jpaRepository.existsBySection_IdAndLessonIsNull(sectionId);
    }

    @Override
    public Optional<Quiz> findByCourseIdOnly(Long courseId) {
        return jpaRepository.findByCourse_IdAndSectionIsNullAndLessonIsNull(courseId);
    }

    @Override
    public Optional<Quiz> findBySectionIdOnly(Long sectionId) {
        return jpaRepository.findBySection_IdAndLessonIsNull(sectionId);
    }

    @Override
    public void delete(Quiz quiz) {
        jpaRepository.delete(quiz);
    }
}
