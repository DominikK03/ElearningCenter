package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.UnpublishCourseCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.email.EmailService;

@Service
public class UnpublishCourseCommandHandler {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UnpublishCourseCommandHandler(
            CourseRepository courseRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void handle(UnpublishCourseCommand command){
        Course course = command.isAdmin()
                ? courseRepository.findByIdOrThrow(command.courseId())
                : courseRepository.findByIdAndInstructorIdOrThrow(command.courseId(), command.actorId());
        course.unpublish();

        if (command.isAdmin()) {
            User instructor = userRepository.findByIdOrThrow(course.getInstructorId());
            emailService.sendCourseModerationEmail(
                    instructor.getEmail().getValue(),
                    instructor.getUsername().getValue(),
                    course.getTitle().getValue(),
                    command.reason(),
                    "unpublished"
            );
        }
    }
}
