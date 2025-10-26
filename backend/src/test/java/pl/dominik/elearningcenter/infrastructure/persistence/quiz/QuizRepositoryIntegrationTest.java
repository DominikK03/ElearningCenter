package pl.dominik.elearningcenter.infrastructure.persistence.quiz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.dominik.elearningcenter.domain.quiz.Question;
import pl.dominik.elearningcenter.domain.quiz.QuestionType;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizAttempt;
import pl.dominik.elearningcenter.domain.quiz.exception.QuizNotFoundException;
import pl.dominik.elearningcenter.domain.quiz.valueobject.Answer;
import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({QuizRepositoryAdapter.class, QuizAttemptRepositoryAdapter.class})
class QuizRepositoryIntegrationTest {

    @Autowired
    private QuizRepositoryAdapter quizRepository;

    @Autowired
    private QuizAttemptRepositoryAdapter attemptRepository;

    @Autowired
    private QuizJpaRepository quizJpaRepository;

    @Autowired
    private QuizAttemptJpaRepository attemptJpaRepository;

    @Test
    void shouldSaveAndRetrieveQuiz() {
        Quiz quiz = Quiz.create("Integration Test Quiz", 70, 1L);
        quizRepository.save(quiz);

        Optional<Quiz> retrieved = quizRepository.findById(quiz.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Integration Test Quiz");
        assertThat(retrieved.get().getPassingScore()).isEqualTo(70);
        assertThat(retrieved.get().getInstructorId()).isEqualTo(1L);
    }

    @Test
    void shouldSaveQuizWithQuestions() {
        Quiz quiz = Quiz.create("Quiz with Questions", 70, 1L);

        Question q1 = new Question("Question 1", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        quiz.addQuestion(q1);

        Question q2 = new Question("Question 2", QuestionType.MULTIPLE_CHOICE, 1);
        q2.setAnswers(List.of(
                Answer.of("Option 1", true),
                Answer.of("Option 2", false),
                Answer.of("Option 3", true)
        ));
        quiz.addQuestion(q2);

        quizRepository.save(quiz);

        Quiz retrieved = quizRepository.findByIdOrThrow(quiz.getId());

        assertThat(retrieved.getQuestions()).hasSize(2);
        assertThat(retrieved.getQuestions().get(0).getText()).isEqualTo("Question 1");
        assertThat(retrieved.getQuestions().get(1).getText()).isEqualTo("Question 2");
        assertThat(retrieved.getQuestions().get(0).getAnswers()).hasSize(2);
        assertThat(retrieved.getQuestions().get(1).getAnswers()).hasSize(3);
    }

    @Test
    void shouldUpdateQuizTitle() {
        Quiz quiz = Quiz.create("Original Title", 70, 1L);
        quizRepository.save(quiz);

        Quiz retrieved = quizRepository.findByIdOrThrow(quiz.getId());
        retrieved.updateTitle("Updated Title");
        quizRepository.save(retrieved);

        Quiz updated = quizRepository.findByIdOrThrow(quiz.getId());
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void shouldDeleteQuiz() {
        Quiz quiz = Quiz.create("Quiz to Delete", 70, 1L);
        quizRepository.save(quiz);

        Long quizId = quiz.getId();
        quizRepository.delete(quiz);

        Optional<Quiz> retrieved = quizRepository.findById(quizId);
        assertThat(retrieved).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenQuizNotFound() {
        assertThatThrownBy(() -> quizRepository.findByIdOrThrow(999L))
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessageContaining("Quiz not found: 999");
    }

    @Test
    void shouldSaveAndRetrieveQuizAttempt() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        Question q = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        quiz.addQuestion(q);
        quizRepository.save(quiz);

        QuizAttempt attempt = QuizAttempt.create(
                quiz.getId(),
                5L,
                8,
                10,
                true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );
        attemptRepository.save(attempt);

        Optional<QuizAttempt> retrieved = attemptRepository.findById(attempt.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getQuizId()).isEqualTo(quiz.getId());
        assertThat(retrieved.get().getStudentId()).isEqualTo(5L);
        assertThat(retrieved.get().getScore()).isEqualTo(8);
        assertThat(retrieved.get().getMaxScore()).isEqualTo(10);
        assertThat(retrieved.get().isPassed()).isTrue();
    }

    @Test
    void shouldFindAllAttemptsByQuizId() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        Question q = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        quiz.addQuestion(q);
        quizRepository.save(quiz);

        QuizAttempt attempt1 = QuizAttempt.create(
                quiz.getId(), 5L, 8, 10, true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );
        QuizAttempt attempt2 = QuizAttempt.create(
                quiz.getId(), 5L, 9, 10, true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );
        QuizAttempt attempt3 = QuizAttempt.create(
                quiz.getId(), 6L, 5, 10, false,
                List.of(StudentAnswer.of(q.getId(), List.of(1)))
        );

        attemptRepository.save(attempt1);
        attemptRepository.save(attempt2);
        attemptRepository.save(attempt3);

        List<QuizAttempt> attempts = attemptRepository.findByQuizId(quiz.getId());

        assertThat(attempts).hasSize(3);
    }

    @Test
    void shouldFindAttemptsByQuizIdAndStudentId() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        Question q = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        quiz.addQuestion(q);
        quizRepository.save(quiz);

        QuizAttempt attempt1 = QuizAttempt.create(
                quiz.getId(), 5L, 8, 10, true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );
        QuizAttempt attempt2 = QuizAttempt.create(
                quiz.getId(), 5L, 9, 10, true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );
        QuizAttempt attempt3 = QuizAttempt.create(
                quiz.getId(), 6L, 5, 10, false,
                List.of(StudentAnswer.of(q.getId(), List.of(1)))
        );

        attemptRepository.save(attempt1);
        attemptRepository.save(attempt2);
        attemptRepository.save(attempt3);

        List<QuizAttempt> studentAttempts = attemptRepository.findByQuizIdAndStudentId(
                quiz.getId(), 5L
        );

        assertThat(studentAttempts).hasSize(2);
        assertThat(studentAttempts).allMatch(a -> a.getStudentId().equals(5L));
    }

    @Test
    void shouldFindBestAttemptByQuizIdAndStudentId() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);
        Question q = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        quiz.addQuestion(q);
        quizRepository.save(quiz);

        QuizAttempt attempt1 = QuizAttempt.create(
                quiz.getId(), 5L, 6, 10, false,
                List.of(StudentAnswer.of(q.getId(), List.of(1)))
        );
        QuizAttempt attempt2 = QuizAttempt.create(
                quiz.getId(), 5L, 9, 10, true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );
        QuizAttempt attempt3 = QuizAttempt.create(
                quiz.getId(), 5L, 7, 10, true,
                List.of(StudentAnswer.of(q.getId(), List.of(0)))
        );

        attemptRepository.save(attempt1);
        attemptRepository.save(attempt2);
        attemptRepository.save(attempt3);

        Optional<QuizAttempt> bestAttempt = attemptRepository.findBestAttempt(
                quiz.getId(), 5L
        );

        assertThat(bestAttempt).isPresent();
        assertThat(bestAttempt.get().getScore()).isEqualTo(9);
    }

    @Test
    void shouldReturnEmptyWhenNoBestAttemptExists() {
        Optional<QuizAttempt> bestAttempt = attemptRepository.findBestAttempt(
                999L, 5L
        );

        assertThat(bestAttempt).isEmpty();
    }

    @Test
    void shouldPreserveQuestionOrderWhenSaving() {
        Quiz quiz = Quiz.create("Ordered Quiz", 70, 1L);

        Question q1 = new Question("First", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true)));

        Question q2 = new Question("Second", QuestionType.SINGLE_CHOICE, 1);
        q2.setAnswers(List.of(Answer.of("A", true)));

        Question q3 = new Question("Third", QuestionType.SINGLE_CHOICE, 2);
        q3.setAnswers(List.of(Answer.of("A", true)));

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);

        quizRepository.save(quiz);

        Quiz retrieved = quizRepository.findByIdOrThrow(quiz.getId());

        assertThat(retrieved.getQuestions()).hasSize(3);
        assertThat(retrieved.getQuestions().get(0).getText()).isEqualTo("First");
        assertThat(retrieved.getQuestions().get(1).getText()).isEqualTo("Second");
        assertThat(retrieved.getQuestions().get(2).getText()).isEqualTo("Third");
    }

    @Test
    void shouldUpdateQuizWithAddedQuestions() {
        Quiz quiz = Quiz.create("Growing Quiz", 70, 1L);
        quizRepository.save(quiz);

        Quiz retrieved = quizRepository.findByIdOrThrow(quiz.getId());
        Question q = new Question("New Question", QuestionType.SINGLE_CHOICE, 0);
        q.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));
        retrieved.addQuestion(q);
        quizRepository.save(retrieved);

        Quiz updated = quizRepository.findByIdOrThrow(quiz.getId());
        assertThat(updated.getQuestions()).hasSize(1);
        assertThat(updated.getQuestions().get(0).getText()).isEqualTo("New Question");
    }

    @Test
    void shouldRemoveQuestionFromQuiz() {
        Quiz quiz = Quiz.create("Quiz to Shrink", 70, 1L);

        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true)));

        Question q2 = new Question("Q2", QuestionType.SINGLE_CHOICE, 1);
        q2.setAnswers(List.of(Answer.of("A", true)));

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quizRepository.save(quiz);

        Quiz retrieved = quizRepository.findByIdOrThrow(quiz.getId());
        Long q1Id = retrieved.getQuestions().get(0).getId();
        retrieved.removeQuestion(q1Id);
        quizRepository.save(retrieved);

        Quiz updated = quizRepository.findByIdOrThrow(quiz.getId());
        assertThat(updated.getQuestions()).hasSize(1);
        assertThat(updated.getQuestions().get(0).getText()).isEqualTo("Q2");
    }

    @Test
    void shouldPersistStudentAnswersWithQuizAttempt() {
        Quiz quiz = Quiz.create("Test Quiz", 70, 1L);

        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q1.setAnswers(List.of(Answer.of("A", true), Answer.of("B", false)));

        Question q2 = new Question("Q2", QuestionType.MULTIPLE_CHOICE, 1);
        q2.setAnswers(List.of(
                Answer.of("1", true),
                Answer.of("2", false),
                Answer.of("3", true)
        ));

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quizRepository.save(quiz);

        List<StudentAnswer> studentAnswers = List.of(
                StudentAnswer.of(q1.getId(), List.of(0)),
                StudentAnswer.of(q2.getId(), List.of(0, 2))
        );

        QuizAttempt attempt = QuizAttempt.create(
                quiz.getId(), 5L, 10, 10, true, studentAnswers
        );
        attemptRepository.save(attempt);

        QuizAttempt retrieved = attemptRepository.findByIdOrThrow(attempt.getId());

        assertThat(retrieved.getAnswers()).hasSize(2);
        assertThat(retrieved.getAnswers().get(0).getQuestionId()).isEqualTo(q1.getId());
        assertThat(retrieved.getAnswers().get(0).getSelectedAnswerIndexes()).containsExactly(0);
        assertThat(retrieved.getAnswers().get(1).getQuestionId()).isEqualTo(q2.getId());
        assertThat(retrieved.getAnswers().get(1).getSelectedAnswerIndexes()).containsExactlyInAnyOrder(0, 2);
    }
}
