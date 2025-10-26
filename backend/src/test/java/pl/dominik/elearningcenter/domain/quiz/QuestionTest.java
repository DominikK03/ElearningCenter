package pl.dominik.elearningcenter.domain.quiz;

import org.junit.jupiter.api.Test;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class QuestionTest {

    @Test
    void shouldCreateQuestionSuccessfully() {
        String text = "What is Java?";
        QuestionType type = QuestionType.SINGLE_CHOICE;
        Integer orderIndex = 0;

        Question question = new Question(text, type, orderIndex);

        assertThat(question.getText()).isEqualTo(text);
        assertThat(question.getType()).isEqualTo(type);
        assertThat(question.getOrderIndex()).isEqualTo(0);
        assertThat(question.getPoints()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenTextIsEmpty() {
        assertThatThrownBy(() -> new Question("", QuestionType.SINGLE_CHOICE, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question text cannot be empty");
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull()  {
        assertThatThrownBy(() -> new Question("Test?", null , 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question type cannot be null");
    }

    @Test
    void shouldUpdateTextSuccessfully() {
        Question question = new Question("Old text", QuestionType.SINGLE_CHOICE, 0);

        question.updateText("New text");

        assertThat(question.getText()).isEqualTo("New text");
    }

    @Test
    void shouldThrowExceptionWhenPointsLessThanOne() {
        Question question = new Question("Test", QuestionType.SINGLE_CHOICE, 0);

        assertThatThrownBy(() -> question.updatePoints(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Points must be >= 1");
    }

    @Test
    void shouldValidateSingleChoiceCorrectAnswer() {
        Question question = new Question("2 + 2 = ?", QuestionType.SINGLE_CHOICE, 0);
        List<Answer> answers = List.of(
                Answer.of("3", false),
                Answer.of("4", true),
                Answer.of("5", false)
        );
        question.setAnswers(answers);

        assertThat(question.isAnswerCorrect(List.of(1))).isTrue();
        assertThat(question.isAnswerCorrect(List.of(0))).isFalse();
        assertThat(question.isAnswerCorrect(List.of(2))).isFalse();
    }

    @Test
    void shouldValdiateMultipleChoiceCorrectAnswer() {
        Question question = new Question("Select even numbers", QuestionType.MULTIPLE_CHOICE, 0);
        List<Answer> answers = List.of(
                Answer.of("1", false),
                Answer.of("2", true),
                Answer.of("3", false),
                Answer.of("4", true)
        );
        question.setAnswers(answers);

        assertThat(question.isAnswerCorrect(List.of(1, 3))).isTrue();
        assertThat(question.isAnswerCorrect(List.of(3, 1))).isTrue();
        assertThat(question.isAnswerCorrect(List.of(1))).isFalse();
        assertThat(question.isAnswerCorrect(List.of(0, 1, 3))).isFalse();
    }

    @Test
    void shouldThrowExceptionForSingleChoiceWithMultipleCorrectAnswers() {
        Question question = new Question("Test", QuestionType.SINGLE_CHOICE, 0);

        List<Answer> invalidAnswers = List.of(
                Answer.of("A", true),
                Answer.of("b", true)
        );

        assertThatThrownBy(() -> question.setAnswers(invalidAnswers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Single choice question must have exacly one correct answer");
    }
    @Test
    void shouldThrowExceptionForSingleChoiceWithNoCorrectAnswers() {
        Question question = new Question("Test", QuestionType.TRUE_FALSE, 0);
        List<Answer> invalidAnswers = List.of(
                Answer.of("True", true),
                Answer.of("False", false),
                Answer.of("Maybe", false)
        );

        assertThatThrownBy(() -> question.setAnswers(invalidAnswers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("True/False question must have exacly 2 answers");
    }

    @Test
    void shouldThrowExceptionWhenAnswersAreEmpty() {
        Question question = new Question("Test", QuestionType.SINGLE_CHOICE, 0);

        assertThatThrownBy(() -> question.setAnswers(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question must have at least one answer");
    }
}
