package pl.dominik.elearningcenter.application.quiz.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.quiz.command.AddQuestionCommand;
import pl.dominik.elearningcenter.application.quiz.command.UpdateQuestionCommand;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.QuestionType;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionCommandHandlerTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private UpdateQuestionCommandHandler handler;

    @Test
    void shouldUpdateQuestionSuccessfully() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Old Text", QuestionType.SINGLE_CHOICE, 0);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L,
                10L,
                "Updated Text",
                0,
                1,
                List.of(
                        new AddQuestionCommand.AnswerInput("New A", true),
                        new AddQuestionCommand.AnswerInput("New B", false)
                ),
                100L
        );

        handler.handle(input);

        Question updated = quiz.findQuestion(10L);
        assertThat(updated.getText()).isEqualTo("Updated Text");
        assertThat(updated.getPoints()).isEqualTo(1);
        assertThat(updated.getAnswers()).hasSize(2);
        assertThat(updated.getAnswers().get(0).getText()).isEqualTo("New A");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotOwner() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.SINGLE_CHOICE, 0);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 0, 1,
                List.of(new AddQuestionCommand.AnswerInput("A", true)),
                999L
        );

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Only course owner can perform this action");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionForSingleChoiceWithNoCorrectAnswer() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.SINGLE_CHOICE, 0);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 0, 1,
                List.of(
                        new AddQuestionCommand.AnswerInput("A", false),
                        new AddQuestionCommand.AnswerInput("B", false)
                ),
                100L
        );

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one correct answer");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionForSingleChoiceWithMultipleCorrectAnswers() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.SINGLE_CHOICE, 0);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 0, 1,
                List.of(
                        new AddQuestionCommand.AnswerInput("A", true),
                        new AddQuestionCommand.AnswerInput("B", true)
                ),
                100L
        );

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exacly one");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionForMultipleChoiceWithNoCorrectAnswer() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.MULTIPLE_CHOICE, 0);
        question.setAnswers(List.of(
                Answer.of("A", true),
                Answer.of("B", false),
                Answer.of("C", true)
        ));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 0, 1,
                List.of(
                        new AddQuestionCommand.AnswerInput("A", false),
                        new AddQuestionCommand.AnswerInput("B", false),
                        new AddQuestionCommand.AnswerInput("C", false)
                ),
                100L
        );

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have at least one correct answer");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldAllowMultipleCorrectAnswersForMultipleChoice() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.MULTIPLE_CHOICE, 0);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Select even numbers", 0, 2,
                List.of(
                        new AddQuestionCommand.AnswerInput("2", true),
                        new AddQuestionCommand.AnswerInput("3", false),
                        new AddQuestionCommand.AnswerInput("4", true),
                        new AddQuestionCommand.AnswerInput("5", false)
                ),
                100L
        );

        assertThatCode(() -> handler.handle(input)).doesNotThrowAnyException();

        Question updated = quiz.findQuestion(10L);
        assertThat(updated.getAnswers()).hasSize(4);
        long correctCount = updated.getAnswers().stream().filter(Answer::isCorrect).count();
        assertThat(correctCount).isEqualTo(2);

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionForTrueFalseWithoutExactlyOneCorrect() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.TRUE_FALSE, 0);
        question.setAnswers(List.of(Answer.of("True", true), Answer.of("False", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 0, 1,
                List.of(
                        new AddQuestionCommand.AnswerInput("True", true),
                        new AddQuestionCommand.AnswerInput("False", true)
                ),
                100L
        );

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exacly one");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldUpdateQuestionOrderIndex() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.SINGLE_CHOICE, 0);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 5, 1,
                List.of(
                        new AddQuestionCommand.AnswerInput("A", true),
                        new AddQuestionCommand.AnswerInput("B", false)
                ),
                100L
        );

        handler.handle(input);

        Question updated = quiz.findQuestion(10L);
        assertThat(updated.getOrderIndex()).isEqualTo(5);

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldUpdateQuestionPoints() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question question = new Question("Text", QuestionType.SINGLE_CHOICE, 0);
        question.updatePoints(1);
        question.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(question, "id", 10L);
        quiz.addQuestion(question);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 10L, "Text", 0, 10,
                List.of(
                        new AddQuestionCommand.AnswerInput("A", true),
                        new AddQuestionCommand.AnswerInput("B", false)
                ),
                100L
        );

        handler.handle(input);

        Question updated = quiz.findQuestion(10L);
        assertThat(updated.getPoints()).isEqualTo(10);

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionWhenQuestionNotFound() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        UpdateQuestionCommand input = new UpdateQuestionCommand(
                1L, 999L, "Text", 0, 1,
                List.of(new AddQuestionCommand.AnswerInput("A", true)),
                100L
        );

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Question not found");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }
}
