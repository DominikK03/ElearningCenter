package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.email.EmailService;

import java.util.UUID;

@Service
public class ResendVerificationEmailCommandHandler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public ResendVerificationEmailCommandHandler(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void handle(ResendVerificationEmailCommand command) {
        User user = userRepository.findByEmail(new Email(command.email()))
                .orElseThrow(() -> new DomainException("User not found"));

        if (user.isEmailVerified()) {
            throw new DomainException("Email already verified");
        }

        String verificationToken = UUID.randomUUID().toString();
        user.generateVerificationToken(verificationToken, 24);
        userRepository.save(user);

        emailService.sendVerificationEmail(
                user.getEmail().getValue(),
                user.getUsername().getValue(),
                verificationToken
        );
    }
}