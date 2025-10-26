package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

@Service
public class ChangePasswordCommandHandler {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public ChangePasswordCommandHandler(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Transactional
    public void handle(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean oldPasswordMatches = passwordHashingService.matches(command.oldPassword(), user.getPassword());
        if (!oldPasswordMatches) {
            throw new pl.dominik.elearningcenter.domain.shared.exception.DomainException("Invalid old password");
        }

        Password newHashedPassword = passwordHashingService.hashPassword(command.newPassword());
        user.changePassword(newHashedPassword, true);
    }
}
