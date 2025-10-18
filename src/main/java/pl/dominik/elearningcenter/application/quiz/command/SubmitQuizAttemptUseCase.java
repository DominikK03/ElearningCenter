package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.input.SubmitQuizAttemptInput;
import pl.dominik.elearningcenter.domain.quiz.*;
import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubmitQuizAttemptUseCase {
    private final QuizAttemptRepository attemptRepository;
    private final QuizRepository quizRepository;

    public SubmitQuizAttemptUseCase(QuizAttemptRepository attemptRepository, QuizRepository quizRepository) {
        this.attemptRepository = attemptRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizAttemptDTO execute(SubmitQuizAttemptInput command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        List<StudentAnswer> studentAnswers = command.answers().stream()
                .map(answerInput -> StudentAnswer.of(
                        answerInput.questionId(),
                        answerInput.selectedAnswerIndexes()
                ))
                .toList();

        int score = calculateScore(quiz, studentAnswers);
        int maxScore = quiz.calculateMaxScore();
        boolean passed = quiz.isPassed(score, maxScore);
        QuizAttempt attempt = QuizAttempt.create(
                command.quizId(),
                command.studentId(),
                score,
                maxScore,
                passed,
                studentAnswers
        );
        attemptRepository.save(attempt);
        return QuizAttemptDTO.from(attempt);
    }

    private int calculateScore(Quiz quiz, List<StudentAnswer> studentAnswers) {
        Map<Long, StudentAnswer> answerMap = studentAnswers.stream()
                .collect(Collectors.toMap(
                        StudentAnswer::getQuestionId,
                        sa -> sa
                ));
        int totalScore = 0;

        for (Question question : quiz.getQuestions()) {
            StudentAnswer studentAnswer = answerMap.get(question.getId());

            if (studentAnswer == null) {
                continue;
            }

            boolean isCorrect = question.isAnswerCorrect(
                    studentAnswer.getSelectedAnswerIndexes()
            );

            if (isCorrect) {
                totalScore += question.getPoints();
            }
        }
        return totalScore;
    }
}
