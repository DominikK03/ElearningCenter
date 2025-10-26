package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.email.EmailService;

@Service
public class VerifyEmailCommandHandler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public VerifyEmailCommandHandler(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void handle(VerifyEmailCommand command) {
        User user = userRepository.findByVerificationToken(command.token())
                .orElseThrow(() -> new DomainException("Invalid or expired verification token"));

        user.verifyEmail(command.token());
        userRepository.save(user);

        try {
            emailService.sendWelcomeEmail(
                    user.getEmail().getValue(),
                    user.getUsername().getValue()
            );
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}