package pl.dominik.elearningcenter.domain.quiz;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "quizzes")
public class Quiz extends AggregateRoot<Long> {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "passing_score", nullable = false)
    private int passingScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    protected Quiz() {
        super();
    }

    private Quiz(String title, int passingScore, Long instructorId,
                 pl.dominik.elearningcenter.domain.course.Course course,
                 pl.dominik.elearningcenter.domain.course.Section section,
                 pl.dominik.elearningcenter.domain.course.Lesson lesson) {
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
        this.course = course;
        this.section = section;
        this.lesson = lesson;
        this.createdAt = LocalDateTime.now();
    }

    public static Quiz create(String title, int passingScore, Long instructorId,
                              Course course,
                              Section section,
                              Lesson lesson) {
        return new Quiz(title, passingScore, instructorId, course, section, lesson);
    }

    public static Quiz create(String title, int passingScore, Long instructorId) {
        return new Quiz(title, passingScore, instructorId, null, null, null);
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

    public void assignToCourse(Course course) {
        this.course = course;
        this.section = null;
        this.lesson = null;
    }

    public void assignToSection(Section section) {
        this.section = section;
        this.lesson = null;
    }

    public void assignToLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public void unassign() {
        this.course = null;
        this.section = null;
        this.lesson = null;
    }

    public int calculateMaxScore() {
        return questions.stream()
                .mapToInt(Question::getPoints)
                .sum();
    }

    public int calculateScore(List<StudentAnswer> studentAnswers) {
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(
                        StudentAnswer::getQuestionId,
                        sa -> sa
                ));

        return questions.stream()
                .mapToInt(question -> {
                    StudentAnswer studentAnswer = answerMap.get(question.getId());
                    if (studentAnswer == null) {
                        return 0;
                    }
                    boolean isCorrect = question.isAnswerCorrect(studentAnswer.getSelectedAnswerIndexes());
                    return isCorrect ? question.getPoints() : 0;
                })
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

    public boolean isAssignedToCourse() {
        return course != null;
    }

    public boolean isAssignedToCourse(Long courseId) {
        return this.course != null && this.course.getId().equals(courseId);
    }

    public boolean isAssignedToSection() {
        return section != null;
    }

    public boolean isAssignedToSection(Long sectionId) {
        return this.section != null && this.section.getId().equals(sectionId);
    }

    public boolean isAssignedToLesson() {
        return lesson != null;
    }

    public boolean isAssignedToLesson(Long lessonId) {
        return this.lesson != null && this.lesson.getId().equals(lessonId);
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

    public Long getCourseId() {
        return course != null ? course.getId() : null;
    }

    public Long getSectionId() {
        return section != null ? section.getId() : null;
    }

    public Long getLessonId() {
        return lesson != null ? lesson.getId() : null;
    }

    public Course getCourse() {
        return course;
    }

    public Section getSection() {
        return section;
    }

    public Lesson getLesson() {
        return lesson;
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

