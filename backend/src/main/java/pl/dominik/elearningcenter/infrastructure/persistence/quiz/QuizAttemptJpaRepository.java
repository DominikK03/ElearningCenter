package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;

import java.util.List;
import java.util.Optional;

interface QuizAttemptJpaRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuizIdOrderByAttemptedAtDesc(Long quizId);
    List<QuizAttempt> findByStudentIdOrderByAttemptedAtDesc(Long studentId);
    List<QuizAttempt> findByQuizIdAndStudentIdOrderByAttemptedAtDesc(Long quizId, Long studentId);

    @Query("SELECT qa from QuizAttempt qa where  qa.quizId = :quizId AND qa.studentId = :studentId order by qa.score desc limit 1")
    Optional<QuizAttempt> findBestAttempt(@Param("quizId") Long quizId, @Param("studentId") Long studentId);

    boolean existsByQuizId(Long quizId);

    boolean existsByQuizIdAndStudentId(Long quizId, Long studentId);

    long countByQuizId(Long quizId);

    void deleteByQuizId(Long quizId);
}
