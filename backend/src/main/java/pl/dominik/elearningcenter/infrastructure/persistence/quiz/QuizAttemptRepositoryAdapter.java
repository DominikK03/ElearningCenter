package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import org.springframework.stereotype.Repository;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.QuizAttemptRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuizAttemptRepositoryAdapter implements QuizAttemptRepository {
    private final QuizAttemptJpaRepository jpaRepository;

    public QuizAttemptRepositoryAdapter(QuizAttemptJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public QuizAttempt save(QuizAttempt quizAttempt) {
        return jpaRepository.save(quizAttempt);
    }

    @Override
    public Optional<QuizAttempt> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<QuizAttempt> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<QuizAttempt> findByQuizId(Long quizId) {
        return jpaRepository.findByQuizIdOrderByAttemptedAtDesc(quizId);
    }

    @Override
    public List<QuizAttempt> findByStudentId(Long studentId) {
        return jpaRepository.findByStudentIdOrderByAttemptedAtDesc(studentId);
    }

    @Override
    public List<QuizAttempt> findByQuizIdAndStudentId(Long quizId, Long studentId) {
        return jpaRepository.findByQuizIdAndStudentIdOrderByAttemptedAtDesc(quizId, studentId);
    }

    @Override
    public Optional<QuizAttempt> findBestAttempt(Long quizId, Long studentId) {
        return jpaRepository.findBestAttempt(quizId, studentId);
    }

    @Override
    public boolean existsByQuizId(Long quizId) {
        return jpaRepository.existsByQuizId(quizId);
    }

    @Override
    public boolean existsByQuizIdAndStudentId(Long quizId, Long studentId) {
        return jpaRepository.existsByQuizIdAndStudentId(quizId, studentId);
    }

    @Override
    public long countByQuizId(Long quizId) {
        return jpaRepository.countByQuizId(quizId);
    }

    @Override
    public void delete(QuizAttempt quizAttempt) {
        jpaRepository.delete(quizAttempt);
    }

    @Override
    public void deleteByQuizId(Long quizId) {
        jpaRepository.deleteByQuizId(quizId);
    }
}
