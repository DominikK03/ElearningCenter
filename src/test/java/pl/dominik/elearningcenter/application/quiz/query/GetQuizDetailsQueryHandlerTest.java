package pl.dominik.elearningcenter.application.quiz.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizDetailsQuery;
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
class GetQuizDetailsQueryHandlerTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private GetQuizDetailsQueryHandler handler;

    @Test
    void shouldGetQuizDetailsWhenUserIsOwner() {
        Quiz quiz = Quiz.create("Java Basics", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        Question q1 = new Question("What is Java?", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(
                Answer.of("A programming language", true),
                Answer.of("A coffee brand", false)
        ));
        ReflectionTestUtils.setField(q1, "id", 10L);
        quiz.addQuestion(q1);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        GetQuizDetailsQuery input = new GetQuizDetailsQuery(1L, 100L);
        QuizDTO result = handler.handle(input);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Java Basics");
        assertThat(result.passingScore()).isEqualTo(70);
        assertThat(result.questions()).hasSize(1);
        assertThat(result.questions().get(0).text()).isEqualTo("What is Java?");
        assertThat(result.questions().get(0).answers()).hasSize(2);

        assertThat(result.questions().get(0).answers().get(0).correct()).isTrue();
        assertThat(result.questions().get(0).answers().get(1).correct()).isFalse();

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        Quiz quiz = Quiz.create("Java Basics", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);
        ReflectionTestUtils.setField(quiz, "instructorId", 100L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        GetQuizDetailsQuery input = new GetQuizDetailsQuery(1L, 999L);

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Permission denied")
                .hasMessageContaining("not owner");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldThrowExceptionWhenQuizNotFound() {
        when(quizRepository.findByIdOrThrow(999L))
                .thenThrow(new DomainException("Quiz not found: 999"));

        GetQuizDetailsQuery input = new GetQuizDetailsQuery(999L, 100L);

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Quiz not found");

        verify(quizRepository, times(1)).findByIdOrThrow(999L);
    }

    @Test
    void shouldGetQuizWithMultipleQuestions() {
        Quiz quiz = Quiz.create("Advanced Quiz", 80, 1L);
        ReflectionTestUtils.setField(quiz, "id", 2L);
        ReflectionTestUtils.setField(quiz, "instructorId", 50L);

        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q1, "id", 1L);

        Question q2 = new Question("Q2", QuestionType.MULTIPLE_CHOICE, 1);
        q2.setAnswers(List.of(
                Answer.of("Option 1", true),
                Answer.of("Option 2", false),
                Answer.of("Option 3", true)
        ));
        ReflectionTestUtils.setField(q2, "id", 2L);

        Question q3 = new Question("Q3", QuestionType.TRUE_FALSE, 2);
        q3.setAnswers(List.of(Answer.of("True", true), Answer.of("False", false)));
        ReflectionTestUtils.setField(q3, "id", 3L);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);

        when(quizRepository.findByIdOrThrow(2L)).thenReturn(quiz);

        GetQuizDetailsQuery input = new GetQuizDetailsQuery(2L, 50L);
        QuizDTO result = handler.handle(input);

        assertThat(result.questions()).hasSize(3);
        assertThat(result.questions().get(0).type()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(result.questions().get(1).type()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
        assertThat(result.questions().get(2).type()).isEqualTo(QuestionType.TRUE_FALSE);

        assertThat(result.questions().get(1).answers().get(0).correct()).isTrue();
        assertThat(result.questions().get(1).answers().get(1).correct()).isFalse();
        assertThat(result.questions().get(1).answers().get(2).correct()).isTrue();
    }

    @Test
    void shouldGetQuizWithNoQuestions() {
        Quiz quiz = Quiz.create("Empty Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 3L);
        ReflectionTestUtils.setField(quiz, "instructorId", 200L);

        when(quizRepository.findByIdOrThrow(3L)).thenReturn(quiz);

        GetQuizDetailsQuery input = new GetQuizDetailsQuery(3L, 200L);
        QuizDTO result = handler.handle(input);

        assertThat(result.questions()).isEmpty();
        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.title()).isEqualTo("Empty Quiz");
    }
}
