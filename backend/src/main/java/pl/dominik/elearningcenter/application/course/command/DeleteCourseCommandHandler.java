package pl.dominik.elearningcenter.application.course.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.course.command.DeleteCourseCommand;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.email.EmailService;

@Service
public class DeleteCourseCommandHandler {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public DeleteCourseCommandHandler(
            CourseRepository courseRepository,
            UserRepository userRepository,
            EmailService emailService
    ){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void handle(DeleteCourseCommand command){
        Course course = command.isAdmin()
                ? courseRepository.findByIdOrThrow(command.courseId())
                : courseRepository.findByIdAndInstructorIdOrThrow(command.courseId(), command.actorId());
        String courseTitle = course.getTitle().getValue();
        Long instructorId = course.getInstructorId();
        courseRepository.delete(course);

        if (command.isAdmin()) {
            User instructor = userRepository.findByIdOrThrow(instructorId);
            emailService.sendCourseModerationEmail(
                    instructor.getEmail().getValue(),
                    instructor.getUsername().getValue(),
                    courseTitle,
                    command.reason(),
                    "deleted"
            );
        }
    }
}
