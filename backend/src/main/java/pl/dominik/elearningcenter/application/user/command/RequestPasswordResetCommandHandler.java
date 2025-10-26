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
public class RequestPasswordResetCommandHandler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public RequestPasswordResetCommandHandler(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void handle(RequestPasswordResetCommand command) {
        User user = userRepository.findByEmail(new Email(command.email()))
                .orElseThrow(() -> new DomainException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        user.generatePasswordResetToken(resetToken, 1);

        userRepository.save(user);

        emailService.sendPasswordResetEmail(
                user.getEmail().getValue(),
                user.getUsername().getValue(),
                resetToken
        );
    }
}