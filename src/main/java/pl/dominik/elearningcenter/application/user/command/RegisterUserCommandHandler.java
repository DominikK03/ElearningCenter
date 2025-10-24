package pl.dominik.elearningcenter.application.user.command;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

@Service
public class RegisterUserCommandHandler {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public RegisterUserCommandHandler(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Transactional
    public Long handle(RegisterUserCommand command) {
        Email email = new Email(command.email());
        Username username = new Username(command.username());

        if (userRepository.existsByEmail(email)) {
            throw new DomainException("Email already exists " + command.email());
        }
        if (userRepository.existsByUsername(username)) {
            throw new DomainException("Username already exists " + command.username());
        }
        Password password = passwordHashingService.hashPassword(command.password());

        User user = User.register(username, email, password, command.role());

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}
