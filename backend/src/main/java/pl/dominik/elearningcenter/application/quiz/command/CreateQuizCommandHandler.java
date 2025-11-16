package pl.dominik.elearningcenter.application.quiz.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.quiz.command.CreateQuizCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.quiz.QuizRepository;

@Service
public class CreateQuizCommandHandler {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;

    public CreateQuizCommandHandler(QuizRepository quizRepository, CourseRepository courseRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Long handle(CreateQuizCommand command) {
        Course course = null;
        Section sectionForQuiz = null;
        Lesson lesson = null;

        if (command.lessonId() != null) {
            if (quizRepository.existsByLessonId(command.lessonId())) {
                throw new IllegalStateException("Quiz already exists for this lesson");
            }
            course = courseRepository.findWithSectionsById(command.courseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + command.courseId()));
            Section section = course.findSection(command.sectionId());
            lesson = section.findLesson(command.lessonId());
            sectionForQuiz = null; // lesson-level quizzes should not occupy section slot
        } else if (command.sectionId() != null) {
            if (quizRepository.existsBySectionIdOnly(command.sectionId())) {
                throw new IllegalStateException("Quiz already exists for this section");
            }
            course = courseRepository.findWithSectionsById(command.courseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + command.courseId()));
            sectionForQuiz = course.findSection(command.sectionId());
        } else if (command.courseId() != null) {
            if (quizRepository.existsByCourseIdOnly(command.courseId())) {
                throw new IllegalStateException("Quiz already exists for this course");
            }
            course = courseRepository.findByIdOrThrow(command.courseId());
        }

        Quiz quiz = Quiz.create(
                command.title(),
                command.passingScore(),
                command.instructorId(),
                course,
                sectionForQuiz,
                lesson
        );
        quizRepository.save(quiz);
        return quiz.getId();
    }
}
