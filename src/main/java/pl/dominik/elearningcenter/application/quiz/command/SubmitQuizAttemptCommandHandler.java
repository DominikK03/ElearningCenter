package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.dto.QuizAttemptDTO;
import pl.dominik.elearningcenter.application.quiz.mapper.QuizAttemptMapper;
import pl.dominik.elearningcenter.application.quiz.command.SubmitQuizAttemptCommand;
import pl.dominik.elearningcenter.domain.quiz.*;
import pl.dominik.elearningcenter.domain.quiz.valueobject.StudentAnswer;

import java.util.List;

@Service
public class SubmitQuizAttemptCommandHandler {
    private final QuizAttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptMapper quizAttemptMapper;

    public SubmitQuizAttemptCommandHandler(
            QuizAttemptRepository attemptRepository,
            QuizRepository quizRepository,
            QuizAttemptMapper quizAttemptMapper
    ) {
        this.attemptRepository = attemptRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptMapper = quizAttemptMapper;
    }

    @Transactional
    public QuizAttemptDTO handle(SubmitQuizAttemptCommand command) {
        Quiz quiz = quizRepository.findByIdOrThrow(command.quizId());
        List<StudentAnswer> studentAnswers = command.answers().stream()
                .map(answerInput -> StudentAnswer.of(
                        answerInput.questionId(),
                        answerInput.selectedAnswerIndexes()
                ))
                .toList();

        int score = quiz.calculateScore(studentAnswers);
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
        return quizAttemptMapper.toDto(attempt);
    }
}
