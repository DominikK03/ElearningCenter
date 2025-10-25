package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

@Service
public class ResetPasswordCommandHandler {

    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public ResetPasswordCommandHandler(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Transactional
    public void handle(ResetPasswordCommand command) {
        User user = userRepository.findByPasswordResetToken(command.token())
                .orElseThrow(() -> new DomainException("Invalid or expired password reset token"));

        Password newHashedPassword = passwordHashingService.hashPassword(command.newPassword());
        user.resetPassword(command.token(), newHashedPassword);

        userRepository.save(user);
    }
}