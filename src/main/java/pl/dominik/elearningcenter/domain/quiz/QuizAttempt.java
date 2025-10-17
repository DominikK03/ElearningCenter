package pl.dominik.elearningcenter.domain.quiz;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt extends AggregateRoot<Long> {

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private int score;

    @Column(name = "max_score", nullable = false)
    private int maxScore;

    @Column(nullable = false)
    private boolean passed;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private LocalDateTime attemptedAt;

    @ElementCollection
    @CollectionTable(name = "quiz_attempt_answers", joinColumns = @JoinColumn(name = "quiz_attempt_id"))
    private List<StudentAnswer> answers = new ArrayList<>();

    protected QuizAttempt() {
        super();
    }

    private QuizAttempt(
            Long quizId,
            Long studentId,
            int score,
            int maxScore,
            boolean passed,
            List<StudentAnswer> answers
    ) {
        if (quizId == null) {
            throw new IllegalArgumentException("Quiz ID cannot be null");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        if (maxScore < 0) {
            throw new IllegalArgumentException("Max score cannot be negative");
        }
        if (score > maxScore) {
            throw new IllegalArgumentException("Score cannot exceed max score");
        }
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Answers cannot be empty");
        }
        this.quizId = quizId;
        this.studentId = studentId;
        this.score = score;
        this.maxScore = maxScore;
        this.passed = passed;
        this.answers = new ArrayList<>(answers);
        this.attemptedAt = LocalDateTime.now();
    }

    public static QuizAttempt create(
            Long quizId,
            Long studentId,
            int score,
            int maxScore,
            boolean passed,
            List<StudentAnswer> answers
    ) {
        return new QuizAttempt(quizId, studentId, score, maxScore, passed, answers);
    }

    public int getScorePercentage() {
        if (maxScore == 0) {
            return 0;
        }
        return (score * 100) / maxScore;
    }

    public boolean belongsToStudent(Long studentId) {
        return this.studentId.equals(studentId);
    }

    public boolean isForQuiz(Long quizId) {
        return this.quizId.equals(quizId);
    }

    public boolean isBetterThan(QuizAttempt other) {
        if (other == null) {
            return true;
        }
        return this.score > other.score;
    }

    public Long getQuizId() {
        return quizId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public boolean isPassed() {
        return passed;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public List<StudentAnswer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }
}
