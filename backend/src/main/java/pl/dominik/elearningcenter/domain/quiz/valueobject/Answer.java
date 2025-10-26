package pl.dominik.elearningcenter.domain.quiz.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public final class Answer {

    @Column(name = "answer_text", nullable = false, length = 500)
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    protected Answer() {}

    private Answer(String text, boolean correct){
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Answer text cannot be empty");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("Answer text cannot exceed 500 characters");
        }
        this.text = text;
        this.correct = correct;
    }
    public static Answer of(String text, boolean correct) {
        return new Answer(text, correct);
    }

    public static Answer correctAnswer(String text) {
        return new Answer(text, true);
    }

    public static Answer incorrectAnswer(String text) {
        return new Answer(text, false);
    }

    public boolean isCorrect(){
        return correct;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return correct == answer.correct && Objects.equals(text, answer.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, correct);
    }

    @Override
    public String toString() {
        return text + " (correct: " + correct + ")";
    }


}
