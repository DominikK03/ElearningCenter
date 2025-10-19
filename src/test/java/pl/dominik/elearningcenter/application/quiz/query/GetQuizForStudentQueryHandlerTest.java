package pl.dominik.elearningcenter.application.quiz.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.quiz.dto.QuizDTO;
import pl.dominik.elearningcenter.application.quiz.query.GetQuizForStudentQuery;
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
class GetQuizForStudentQueryHandlerTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private GetQuizForStudentQueryHandler handler;

    @Test
    void shouldHideCorrectAnswersFromStudent() {
        Quiz quiz = Quiz.create("Student Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 1L);

        Question q1 = new Question("What is 2+2?", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(
                Answer.of("3", false),
                Answer.of("4", true),
                Answer.of("5", false)
        ));
        ReflectionTestUtils.setField(q1, "id", 10L);
        quiz.addQuestion(q1);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(1L, 5L);
        QuizDTO result = handler.handle(input);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Student Quiz");
        assertThat(result.questions()).hasSize(1);

        assertThat(result.questions().get(0).answers()).hasSize(3);
        assertThat(result.questions().get(0).answers())
                .allMatch(answer -> !answer.correct(), "All answers must be hidden from student");

        assertThat(result.questions().get(0).answers().get(0).text()).isEqualTo("3");
        assertThat(result.questions().get(0).answers().get(1).text()).isEqualTo("4");
        assertThat(result.questions().get(0).answers().get(2).text()).isEqualTo("5");

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
    }

    @Test
    void shouldHideCorrectAnswersInMultipleChoiceQuestions() {
        Quiz quiz = Quiz.create("Multiple Choice Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 2L);

        Question q = new Question("Select even numbers", QuestionType.MULTIPLE_CHOICE, 0);
        q.setAnswers(List.of(
                Answer.of("1", false),
                Answer.of("2", true),   // Correct
                Answer.of("3", false),
                Answer.of("4", true)    // Correct
        ));
        ReflectionTestUtils.setField(q, "id", 20L);
        quiz.addQuestion(q);

        when(quizRepository.findByIdOrThrow(2L)).thenReturn(quiz);

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(2L, 10L);
        QuizDTO result = handler.handle(input);

        assertThat(result.questions().get(0).answers())
                .hasSize(4)
                .allMatch(answer -> !answer.correct(), "No correct answers should be revealed");
    }

    @Test
    void shouldHideCorrectAnswersInTrueFalseQuestions() {
        Quiz quiz = Quiz.create("True/False Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 3L);

        Question q = new Question("Java is a programming language", QuestionType.TRUE_FALSE, 0);
        q.setAnswers(List.of(
                Answer.of("True", true),   // Correct
                Answer.of("False", false)
        ));
        ReflectionTestUtils.setField(q, "id", 30L);
        quiz.addQuestion(q);

        when(quizRepository.findByIdOrThrow(3L)).thenReturn(quiz);

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(3L, 15L);
        QuizDTO result = handler.handle(input);

        assertThat(result.questions().get(0).answers())
                .hasSize(2)
                .allMatch(answer -> !answer.correct(), "True/False correct answer must be hidden");
    }

    @Test
    void shouldHideCorrectAnswersForAllQuestionsInQuiz() {
        Quiz quiz = Quiz.create("Mixed Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 4L);

        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q1, "id", 1L);

        Question q2 = new Question("Q2", QuestionType.MULTIPLE_CHOICE, 1);
        q2.setAnswers(List.of(
                Answer.of("X", true),
                Answer.of("Y", false),
                Answer.of("Z", true)
        ));
        ReflectionTestUtils.setField(q2, "id", 2L);

        Question q3 = new Question("Q3", QuestionType.TRUE_FALSE, 2);
        q3.setAnswers(List.of(Answer.of("True", false), Answer.of("False", true)));
        ReflectionTestUtils.setField(q3, "id", 3L);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);

        when(quizRepository.findByIdOrThrow(4L)).thenReturn(quiz);

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(4L, 20L);
        QuizDTO result = handler.handle(input);

        assertThat(result.questions()).hasSize(3);

        for (var question : result.questions()) {
            assertThat(question.answers())
                    .allMatch(answer -> !answer.correct(),
                            "Question '" + question.text() + "' leaked correct answers!");
        }
    }

    @Test
    void shouldThrowExceptionWhenQuizNotFound() {
        when(quizRepository.findByIdOrThrow(999L))
                .thenThrow(new DomainException("Quiz not found: 999"));

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(999L, 5L);

        assertThatThrownBy(() -> handler.handle(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Quiz not found");

        verify(quizRepository, times(1)).findByIdOrThrow(999L);
    }

    @Test
    void shouldReturnQuizMetadataForStudent() {
        Quiz quiz = Quiz.create("Metadata Test", 75, 1L);
        ReflectionTestUtils.setField(quiz, "id", 5L);
        ReflectionTestUtils.setField(quiz, "lessonId", 100L);

        when(quizRepository.findByIdOrThrow(5L)).thenReturn(quiz);

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(5L, 25L);
        QuizDTO result = handler.handle(input);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.title()).isEqualTo("Metadata Test");
        assertThat(result.passingScore()).isEqualTo(75);
        assertThat(result.lessonId()).isEqualTo(100L);
    }

    @Test
    void shouldWorkWithEmptyQuiz() {
        Quiz quiz = Quiz.create("Empty Quiz", 70, 1L);
        ReflectionTestUtils.setField(quiz, "id", 6L);

        when(quizRepository.findByIdOrThrow(6L)).thenReturn(quiz);

        GetQuizForStudentQuery input = new GetQuizForStudentQuery(6L, 30L);
        QuizDTO result = handler.handle(input);

        assertThat(result.questions()).isEmpty();
        assertThat(result.id()).isEqualTo(6L);
    }
}
