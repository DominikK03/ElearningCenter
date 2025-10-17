package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

import java.util.List;
import java.util.Optional;

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
    public List<Quiz> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Quiz> findByLessonId(Long lessonId) {
        return jpaRepository.findByLessonId(lessonId);
    }

    @Override
    public List<Quiz> findByInstructorId(Long instructorId) {
        return jpaRepository.findByInstructorId(instructorId);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByLessonId(Long lessonId) {
        return jpaRepository.existsByLessonId(lessonId);
    }

    @Override
    public void delete(Quiz quiz) {
        jpaRepository.delete(quiz);
    }
}
