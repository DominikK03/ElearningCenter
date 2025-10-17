package pl.dominik.elearningcenter.domain.quiz.valueobject;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Embeddable
public final class StudentAnswer {

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @ElementCollection
    @CollectionTable(name = "student_answer_indexes", joinColumns = @JoinColumn(name = "quiz_attempt_id"))
    @Column(name = "answer_index")
    private List<Integer> selectedAnswerIndexes = new ArrayList<>();

    protected StudentAnswer() {
    }

    private StudentAnswer(Long questionId, List<Integer> selectedAnswerIndexes) {
        if (questionId == null) throw new IllegalArgumentException("Question ID cannot be null");
        if (selectedAnswerIndexes == null || selectedAnswerIndexes.isEmpty()) {
            throw new IllegalArgumentException("At least one answer must be selected");
        }
        if (selectedAnswerIndexes.stream().anyMatch(idx -> idx < 0)) {
            throw new IllegalArgumentException("Answer indexes must be >= 0");
        }
        this.questionId = questionId;
        this.selectedAnswerIndexes = new ArrayList<>(selectedAnswerIndexes);
    }

    public static StudentAnswer of(Long questionId, List<Integer> selectedAnswerIndexes) {
        return new StudentAnswer(questionId, selectedAnswerIndexes);
    }

    public static StudentAnswer singleChoice(Long questionId, int answerIndex) {
        return new StudentAnswer(questionId, List.of(answerIndex));
    }

    public Long getQuestionId() {
        return questionId;
    }

    public List<Integer> getSelectedAnswerIndexes() {
        return Collections.unmodifiableList(selectedAnswerIndexes);
    }

    public boolean isSingleChoice() {
        return selectedAnswerIndexes.size() == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentAnswer that = (StudentAnswer) o;
        return Objects.equals(questionId, that.questionId) && Objects.equals(selectedAnswerIndexes, that.selectedAnswerIndexes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, selectedAnswerIndexes);
    }

    @Override
    public String toString() {
        return "StudentAnswer{questionId=" + questionId + ", selected=" + selectedAnswerIndexes + "}";
    }
}
