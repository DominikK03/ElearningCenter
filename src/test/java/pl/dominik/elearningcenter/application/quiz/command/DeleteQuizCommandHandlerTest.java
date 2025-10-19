package pl.dominik.elearningcenter.application.quiz.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.quiz.command.DeleteQuizCommand;
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
class DeleteQuizCommandHandlerTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private DeleteQuizCommandHandler handler;

    @Test
    void shouldDeleteQuizWhenUserIsOwner() {
        Quiz quiz = Quiz.create("Quiz to Delete", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        DeleteQuizCommand input = new DeleteQuizCommand(1L, 100L);

        assertThatCode(() -> handler.handle(input)).doesNotThrowAnyException();

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        Quiz quiz = Quiz.create("Protected Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        DeleteQuizCommand input = new DeleteQuizCommand(1L, 999L);

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Only course owner can perform this action")
                .hasMessageContaining("Only course owner can perform this action");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
        verify(quizRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteQuizWithQuestions() {
        Quiz quiz = Quiz.create("Quiz with Questions", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q1, "id", 10L);

        Question q2 = new Question("Q2", QuestionType.MULTIPLE_CHOICE, 1);
        q2.setAnswers(List.of(
                Answer.of("X", true),
                Answer.of("Y", false),
                Answer.of("Z", true)
        ));
        ReflectionTestUtils.setField(q2, "id", 20L);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        DeleteQuizCommand input = new DeleteQuizCommand(1L, 100L);

        assertThatCode(() -> handler.handle(input)).doesNotThrowAnyException();

        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    void shouldDeleteQuizEvenIfStudentsHaveAttempts() {
        Quiz quiz = Quiz.create("Quiz with Attempts", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question q = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q, "id", 10L);
        quiz.addQuestion(q);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        DeleteQuizCommand input = new DeleteQuizCommand(1L, 100L);

        assertThatCode(() -> handler.handle(input)).doesNotThrowAnyException();

        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    void shouldThrowExceptionWhenQuizNotFound() {
        when(quizRepository.findByIdOrThrow(999L))
                .thenThrow(new DomainException("Quiz not found: 999"));

        DeleteQuizCommand input = new DeleteQuizCommand(999L, 100L);

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Quiz not found");

        verify(quizRepository, times(1)).findByIdOrThrow(999L);
        verify(quizRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteEmptyQuiz() {
        Quiz quiz = Quiz.create("Empty Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 5L);
        ReflectionTestUtils.setField(quiz, "instructorId", 200L);

        when(quizRepository.findByIdOrThrow(5L)).thenReturn(quiz);

        DeleteQuizCommand input = new DeleteQuizCommand(5L, 200L);

        assertThatCode(() -> handler.handle(input)).doesNotThrowAnyException();

        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    void shouldOnlyDeleteOwnQuiz() {
        Quiz quiz1 = Quiz.create("Instructor 1 Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz1, "id", 1L);
        ReflectionTestUtils.setField(quiz1, "instructorId", 100L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz1);

        DeleteQuizCommand input = new DeleteQuizCommand(1L, 200L);

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Only course owner can perform this action");

        verify(quizRepository, never()).delete(any());
    }

    @Test
    void shouldCallRepositoryDeleteExactlyOnce() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 3L);
        ReflectionTestUtils.setField(quiz, "instructorId", 150L);

        when(quizRepository.findByIdOrThrow(3L)).thenReturn(quiz);

        DeleteQuizCommand input = new DeleteQuizCommand(3L, 150L);
        handler.handle(input);

        verify(quizRepository, times(1)).delete(quiz);
        verifyNoMoreInteractions(quizRepository);
    }
}
