package pl.dominik.elearningcenter.domain.quiz;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType type;

    @Column(nullable = false)
    private int points = 1;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ElementCollection
    @CollectionTable(name = "question_answers", joinColumns = @JoinColumn(name = "question_id"))
    private List<Answer> answers = new ArrayList<>();

    protected Question() {}

    public Question(String text, QuestionType type, Integer orderIndex) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Question type cannot be null");
        }
        if (orderIndex == null || orderIndex < 0) {
            throw new IllegalArgumentException("Order index must be >= 0");
        }
        this.text = text;
        this.type = type;
        this.orderIndex = orderIndex;
    }

    void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void addAnswer(Answer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }

        if (type == QuestionType.TRUE_FALSE && answers.size() >= 2) {
            throw new IllegalArgumentException("True/False questions can have maximum 2 answers");
        }
        answers.add(answer);
    }

    public void setAnswers(List<Answer> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Question must have at least one answer");
        }

        boolean hasCorrectAnswer = answers.stream().anyMatch(Answer::isCorrect);
        if (!hasCorrectAnswer) {
            throw new IllegalArgumentException("Question must have at least one correct answer");
        }

        if (type == QuestionType.SINGLE_CHOICE) {
            long correctCount = answers.stream().filter(Answer::isCorrect).count();
            if (correctCount != 1) {
                throw new IllegalArgumentException("Single choice question must have exacly one correct answer");
            }
        }
        if (type == QuestionType.TRUE_FALSE) {
            if (answers.size() != 2){
                throw new IllegalArgumentException("True/False question must have exacly 2 answers");
            }
            long correctCount = answers.stream().filter(Answer::isCorrect).count();
            if (correctCount != 1) {
                throw new IllegalArgumentException("True / False question must have exacly one answer");
            }
        }

        this.answers.clear();
        this.answers.addAll(answers);
    }

    public void updateText(String newText) {
        if (newText == null || newText.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        this.text = newText;
    }

    public void updateOrderIndex(Integer newOrderIndex) {
        if (newOrderIndex == null || newOrderIndex < 0) {
            throw new IllegalArgumentException("Order index must be >= 0");
        }
        this.orderIndex = newOrderIndex;
    }

    public void updatePoints(int newPoints) {
        if (newPoints < 1) {
            throw new IllegalArgumentException("Points must be >= 1");
        }
        this.points = newPoints;
    }

    public boolean inAnswerCorrect(List<Integer> selectedIndexes) {
        if (selectedIndexes == null || selectedIndexes.isEmpty()) {
            return false;
        }

        List<Integer> correctIndexes = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).isCorrect()) {
                correctIndexes.add(i);
            }
        }

        return selectedIndexes.size() == correctIndexes.size() && new HashSet<>(correctIndexes).containsAll(selectedIndexes);
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public QuestionType getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

