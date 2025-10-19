package pl.dominik.elearningcenter.application.quiz.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import
        pl.dominik.elearningcenter.application.quiz.command.SubmitQuizAttemptCommand;
import pl.dominik.elearningcenter.domain.quiz.*;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitQuizAttemptCommandHandlerTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository attemptRepository;

    @InjectMocks
    private SubmitQuizAttemptCommandHandler handler;


    @Test
    void shouldGradeQuizWithAllCorrectAnswers() {
        Quiz quiz = createQuizWithThreeQuestions();
        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            ReflectionTestUtils.setField(attempt, "id", 100L);
            return attempt;
        });

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(
                        new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(1)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(2L, List.of(0)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(3L, List.of(1, 3))
                )
        );

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(3);
        assertThat(result.maxScore()).isEqualTo(3);
        assertThat(result.scorePercentage()).isEqualTo(100);
        assertThat(result.passed()).isTrue();
        assertThat(result.quizId()).isEqualTo(1L);
        assertThat(result.studentId()).isEqualTo(5L);

        verify(attemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    void shouldGradeQuizWithPartiallyCorrectAnswers() {
        Quiz quiz = createQuizWithThreeQuestions();
        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(i ->
                i.getArgument(0));

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(
                        new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(1)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(2L, List.of(1)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(3L, List.of(1, 3))
                )
        );

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(2);
        assertThat(result.maxScore()).isEqualTo(3);
        assertThat(result.scorePercentage()).isEqualTo(66);
        assertThat(result.passed()).isFalse();
    }

    @Test
    void shouldGradeQuizWithAllWrongAnswers() {
        Quiz quiz = createQuizWithThreeQuestions();
        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(
                        new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(0)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(2L, List.of(1)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(3L, List.of(0, 2))
                )
        );

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(0);
        assertThat(result.maxScore()).isEqualTo(3);
        assertThat(result.scorePercentage()).isEqualTo(0);
        assertThat(result.passed()).isFalse();
    }

    @Test
    void shouldGradeQuizWithExactPassingScore() {
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Quiz bigQuiz = Quiz.create("Big Quiz", 70, 1L);
        for (int i = 0; i < 10; i++) {
            Question q = new Question("Q" + i, QuestionType.SINGLE_CHOICE,
                    i);
            q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
            ReflectionTestUtils.setField(q, "id", (long) i);
            bigQuiz.addQuestion(q);
        }
        ReflectionTestUtils.setField(bigQuiz, "id", 2L);

        when(quizRepository.findByIdOrThrow(2L)).thenReturn(bigQuiz);

        List<SubmitQuizAttemptCommand.StudentAnswerInput> answers = List.of(
                new SubmitQuizAttemptCommand.StudentAnswerInput(0L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(2L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(3L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(4L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(5L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(6L, List.of(0)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(7L, List.of(1)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(8L, List.of(1)),
                new SubmitQuizAttemptCommand.StudentAnswerInput(9L, List.of(1))
        );

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(2L, 5L,
                answers);

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(7);
        assertThat(result.maxScore()).isEqualTo(10);
        assertThat(result.scorePercentage()).isEqualTo(70);
        assertThat(result.passed()).isTrue();
    }


    @Test
    void shouldFailMultipleChoiceWhenMissingCorrectAnswer() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        Question q = new Question("Select even numbers", QuestionType.MULTIPLE_CHOICE, 0);
        q.setAnswers(List.of(
                Answer.of("1", false),
                Answer.of("2", true),
                Answer.of("3", false),
                Answer.of("4", true)
        ));
        ReflectionTestUtils.setField(q, "id", 1L);
        quiz.addQuestion(q);
        ReflectionTestUtils.setField(quiz, "id", 1L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(1)))
        );

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(0);
        assertThat(result.passed()).isFalse();
    }

    @Test
    void shouldFailMultipleChoiceWhenSelectingExtraWrongAnswer() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        Question q = new Question("Select even",
                QuestionType.MULTIPLE_CHOICE, 0);
        q.setAnswers(List.of(
                Answer.of("1", false),
                Answer.of("2", true),
                Answer.of("3", false),
                Answer.of("4", true)
        ));
        ReflectionTestUtils.setField(q, "id", 1L);
        quiz.addQuestion(q);
        ReflectionTestUtils.setField(quiz, "id", 1L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(i ->
                i.getArgument(0));

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(new SubmitQuizAttemptCommand.StudentAnswerInput(1L,
                        List.of(0, 1, 3)))
        );

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(0);
    }

    @Test
    void shouldHandleDifferentPointValuesPerQuestion() {
        Quiz quiz = Quiz.create("Weighted Quiz", 70, 1L);

        Question q1 = new Question("Easy", QuestionType.SINGLE_CHOICE, 0);
        q1.updatePoints(1);
        q1.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q1, "id", 1L);

        Question q2 = new Question("Medium", QuestionType.SINGLE_CHOICE, 1);
        q2.updatePoints(3);
        q2.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q2, "id", 2L);

        Question q3 = new Question("Hard", QuestionType.SINGLE_CHOICE, 2);
        q3.updatePoints(5);
        q3.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        ReflectionTestUtils.setField(q3, "id", 3L);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);
        ReflectionTestUtils.setField(quiz, "id", 1L);

        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(
                        new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(0)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(2L, List.of(1)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(3L, List.of(0))
                )
        );

        QuizAttemptDTO result = handler.handle(input);

        assertThat(result.score()).isEqualTo(6);
        assertThat(result.maxScore()).isEqualTo(9);
        assertThat(result.scorePercentage()).isEqualTo(66);
        assertThat(result.passed()).isFalse();
    }

    @Test
    void shouldSaveQuizAttemptToRepository() {
        Quiz quiz = createQuizWithThreeQuestions();
        when(quizRepository.findByIdOrThrow(1L)).thenReturn(quiz);
        when(attemptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SubmitQuizAttemptCommand input = new SubmitQuizAttemptCommand(
                1L,
                5L,
                List.of(
                        new SubmitQuizAttemptCommand.StudentAnswerInput(1L, List.of(1)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(2L, List.of(0)),
                        new SubmitQuizAttemptCommand.StudentAnswerInput(3L, List.of(1, 3))
                )
        );

        handler.handle(input);

        verify(quizRepository, times(1)).findByIdOrThrow(1L);
        verify(attemptRepository, times(1)).save(any(QuizAttempt.class));
    }


    private Quiz createQuizWithThreeQuestions() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);

        Question q1 = new Question("2 + 2 = ?", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(
                Answer.of("3", false),
                Answer.of("4", true),
                Answer.of("5", false)
        ));
        ReflectionTestUtils.setField(q1, "id", 1L);

        Question q2 = new Question("Capital of Poland?", QuestionType.SINGLE_CHOICE, 1);
        q2.setAnswers(List.of(
                Answer.of("Warsaw", true),
                Answer.of("Cracow", false)
        ));
        ReflectionTestUtils.setField(q2, "id", 2L);

        Question q3 = new Question("Select even numbers", QuestionType.MULTIPLE_CHOICE, 2);
        q3.setAnswers(List.of(
                Answer.of("1", false),
                Answer.of("2", true),
                Answer.of("3", false),
                Answer.of("4", true)
        ));
        ReflectionTestUtils.setField(q3, "id", 3L);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);

        ReflectionTestUtils.setField(quiz, "id", 1L);
        return quiz;
    }
}