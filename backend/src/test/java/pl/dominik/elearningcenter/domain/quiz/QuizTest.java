package pl.dominik.elearningcenter.domain.quiz;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class QuizTest {

    @Test
    void shouldCreateQuizSuccessfully() {
        String title = "Java Basics Quiz";
        int passingScore = 70;
        Long instructorId = 1L;

        Quiz quiz = Quiz.create(title, passingScore, instructorId);

        assertThat(quiz.getTitle()).isEqualTo(title);
        assertThat(quiz.getPassingScore()).isEqualTo(70);
        assertThat(quiz.getInstructorId()).isEqualTo(1L);
        assertThat(quiz.getLessonId()).isNull();
        assertThat(quiz.getQuestionsCount()).isEqualTo(0);
        assertThat(quiz.isOwnedBy(instructorId)).isTrue();
    }

    @Test
    void shouldCreateQuizWithLessonId() {
        Lesson lesson = new Lesson("Test Lesson", "Content", 0);
        ReflectionTestUtils.setField(lesson, "id", 5L);

        Quiz quiz = Quiz.create("Test", 70, 1L, null, null, lesson);

        assertThat(quiz.getLessonId()).isEqualTo(5L);
        assertThat(quiz.isAssignedToLesson()).isTrue();
        assertThat(quiz.isAssignedToLesson(5L)).isTrue();
        assertThat(quiz.isAssignedToLesson(99L)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        assertThatThrownBy(() -> Quiz.create("", 70, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quiz title cannot be empty");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsTooLong() {
        String longTitle = "A".repeat(201);

        assertThatThrownBy(() -> Quiz.create(longTitle, 70, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quiz title cannot exceed 200 characters");
    }

    @Test
    void shouldThrowExceptionWhenPassingScoreIsNegative() {
        assertThatThrownBy(() -> Quiz.create("Test", -1, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passing score must be between 0 and 100");
    }

    @Test
    void shouldThrowExceptionWhenPassingScoreIsAbove100() {
        assertThatThrownBy(() -> Quiz.create("Test", 101, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passing score must be between 0 and 100");
    }

    @Test
    void shouldThrowExceptionWhenInstructorIdIsNull() {
        assertThatThrownBy(() -> Quiz.create("Test", 70, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Instructor ID cannot be null");
    }


    @Test
    void shouldUpdateTitleSuccessfully() {
        Quiz quiz = Quiz.create("Old Title", 70, 1L);

        quiz.updateTitle("New Title");

        assertThat(quiz.getTitle()).isEqualTo("New Title");
    }

    @Test
    void shouldUpdatePassingScoreSuccessfully() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        quiz.updatePassingScore(85);

        assertThat(quiz.getPassingScore()).isEqualTo(85);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTitleToEmpty() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThatThrownBy(() -> quiz.updateTitle(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quiz title cannot be empty");
    }


    @Test
    void shouldAddQuestionToQuiz() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        Question question = new Question("What is Java?",
                QuestionType.SINGLE_CHOICE, 0);

        quiz.addQuestion(question);

        assertThat(quiz.getQuestionsCount()).isEqualTo(1);
        assertThat(quiz.getQuestions()).contains(question);
    }

    @Test
    void shouldAddMultipleQuestions() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        Question q2 = new Question("Q2", QuestionType.MULTIPLE_CHOICE, 1);
        Question q3 = new Question("Q3", QuestionType.TRUE_FALSE, 2);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);

        assertThat(quiz.getQuestionsCount()).isEqualTo(3);
        assertThat(quiz.getQuestions()).containsExactly(q1, q2, q3);
    }

    @Test
    void shouldThrowExceptionWhenAddingNullQuestion() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThatThrownBy(() -> quiz.addQuestion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question cannot be null");
    }

    @Test
    void shouldFindQuestionById() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        Question question = new Question("Test?", QuestionType.SINGLE_CHOICE, 0);
        quiz.addQuestion(question);

        org.springframework.test.util.ReflectionTestUtils.setField(question, "id", 10L);

        Question found = quiz.findQuestion(10L);

        assertThat(found).isEqualTo(question);
    }

    @Test
    void shouldThrowExceptionWhenQuestionNotFound() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThatThrownBy(() -> quiz.findQuestion(999L))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Question not found: 999");
    }

    @Test
    void shouldRemoveQuestion() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        Question question = new Question("Test?", QuestionType.SINGLE_CHOICE, 0);
        quiz.addQuestion(question);
        org.springframework.test.util.ReflectionTestUtils.setField(question, "id", 10L);

        quiz.removeQuestion(10L);

        assertThat(quiz.getQuestionsCount()).isEqualTo(0);
        assertThat(quiz.getQuestions()).doesNotContain(question);
    }


    @Test
    void shouldCalculateMaxScoreCorrectly() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        Question q1 = new Question("Q1", QuestionType.SINGLE_CHOICE, 0);
        q1.updatePoints(2);

        Question q2 = new Question("Q2", QuestionType.MULTIPLE_CHOICE, 1);
        q2.updatePoints(3);

        Question q3 = new Question("Q3", QuestionType.TRUE_FALSE, 2);
        q3.updatePoints(1);

        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        quiz.addQuestion(q3);

        int maxScore = quiz.calculateMaxScore();

        assertThat(maxScore).isEqualTo(6);
    }

    @Test
    void shouldReturnZeroMaxScoreForQuizWithoutQuestions() {
        Quiz quiz = Quiz.create("Empty Quiz", 70, 1L);

        int maxScore = quiz.calculateMaxScore();

        assertThat(maxScore).isEqualTo(0);
    }

    @Test
    void shouldDetermineIfQuizIsPassed() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThat(quiz.isPassed(7, 10)).isTrue();
        assertThat(quiz.isPassed(8, 10)).isTrue();
        assertThat(quiz.isPassed(9, 10)).isTrue();
        assertThat(quiz.isPassed(6, 10)).isFalse();
        assertThat(quiz.isPassed(5, 10)).isFalse();
    }

    @Test
    void shouldReturnFalseForPassedWhenMaxScoreIsZero() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThat(quiz.isPassed(0, 0)).isFalse();
    }

    @Test
    void shouldHandleDifferentPassingScores() {
        Quiz easyQuiz = Quiz.create("Easy", 50, 1L);
        Quiz hardQuiz = Quiz.create("Hard", 90, 1L);

        assertThat(easyQuiz.isPassed(5, 10)).isTrue();
        assertThat(hardQuiz.isPassed(5, 10)).isFalse();

        assertThat(easyQuiz.isPassed(9, 10)).isTrue();
        assertThat(hardQuiz.isPassed(9, 10)).isTrue();
    }

    @Test
    void shouldVerifyOwnership() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThat(quiz.isOwnedBy(1L)).isTrue();
        assertThat(quiz.isOwnedBy(2L)).isFalse();
        assertThat(quiz.isOwnedBy(999L)).isFalse();
    }

    @Test
    void shouldEnsureOwnership() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        assertThatCode(() -> quiz.ensureOwnedBy(1L))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> quiz.ensureOwnedBy(2L))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Only course owner can perform this action");
    }


    @Test
    void shouldAssignToLesson() {
        Quiz quiz = Quiz.create("Test", 70, 1L);

        Lesson lesson = new Lesson("Test Lesson", "Content", 0);
        ReflectionTestUtils.setField(lesson, "id", 5L);

        quiz.assignToLesson(lesson);

        assertThat(quiz.getLessonId()).isEqualTo(5L);
        assertThat(quiz.isAssignedToLesson()).isTrue();
    }

    @Test
    void shouldUnassignFromLesson() {
        Lesson lesson = new Lesson("Test Lesson", "Content", 0);
        ReflectionTestUtils.setField(lesson, "id", 5L);

        Quiz quiz = Quiz.create("Test", 70, 1L, null, null, lesson);

        quiz.unassign();

        assertThat(quiz.getLessonId()).isNull();
        assertThat(quiz.isAssignedToLesson()).isFalse();
    }


    @Test
    void shouldReturnUnmodifiableListOfQuestions() {
        Quiz quiz = Quiz.create("Test", 70, 1L);
        quiz.addQuestion(new Question("Q1", QuestionType.SINGLE_CHOICE, 0));

        List<Question> questions = quiz.getQuestions();

        assertThatThrownBy(() -> questions.add(new Question("Q2", QuestionType.SINGLE_CHOICE, 1)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}