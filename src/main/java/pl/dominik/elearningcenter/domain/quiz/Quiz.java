package pl.dominik.elearningcenter.domain.quiz;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz extends AggregateRoot<Long> {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "passing_score", nullable = false)
    private int passingScore;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    protected Quiz() {
        super();
    }

    private Quiz(String title, int passingScore, Long instructorId, Long lessonId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Quiz title cannot exceed 200 characters");
        }
        if (passingScore < 0 || passingScore > 100) {
            throw new IllegalArgumentException("Passing score must be between 0 and 100");
        }
        if (instructorId == null) {
            throw new IllegalArgumentException("Instructor ID cannot be null");
        }

        this.title = title;
        this.passingScore = passingScore;
        this.instructorId = instructorId;
        this.lessonId = lessonId;
        this.createdAt = LocalDateTime.now();
    }

    public static Quiz create(String title, int passingScore, Long instructorId, Long lessonId) {
        return new Quiz(title, passingScore, instructorId, lessonId);
    }

    public static Quiz create(String title, int passingScore, Long instructorId) {
        return new Quiz(title, passingScore, instructorId, null);
    }

    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        questions.add(question);
        question.setQuiz(this);
    }

    public Question findQuestion(Long questionId) {
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Question not found: " + questionId));
    }

    public void removeQuestion(Long questionId) {
        Question question = findQuestion(questionId);
        questions.remove(question);
    }

    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be empty");
        }
        if (newTitle.length() > 200) {
            throw new IllegalArgumentException("Quiz title cannot exceed 200 characters");
        }
        this.title = newTitle;
    }

    public void updatePassingScore(int newPassingScore) {
        if (newPassingScore < 0 || newPassingScore > 100) {
            throw new IllegalArgumentException("Passing score must be between 0 and 100");
        }
        this.passingScore = newPassingScore;
    }

    public void assingToLesson(Long lessonId) {
        this.lessonId = lessonId;
    }

    public void unassignFromLesson() {
        this.lessonId = null;
    }

    public int calculateMaxScore() {
        return questions.stream()
                .mapToInt(Question::getPoints)
                .sum();
    }

    public boolean isPassed(int score, int maxScore) {
        if (maxScore == 0) {
            return false;
        }
        int percentage = (score * 100) / maxScore;
        return percentage >= passingScore;
    }

    public boolean isOwnedBy(Long instructorId) {
        return this.instructorId.equals(instructorId);
    }

    public void ensureOwnedBy(Long userId){
        if(!isOwnedBy(userId)){
            throw new DomainException("Only course owner can perform this action");
        }
    }

    public boolean isAssignedToLesson() {
        return lessonId != null;
    }

    public boolean isAssignedToLesson(Long lessonId) {
        return this.lessonId != null && this.lessonId.equals(lessonId);
    }

    public int getQuestionsCount() {
        return questions.size();
    }

    public String getTitle() {
        return title;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }
}

