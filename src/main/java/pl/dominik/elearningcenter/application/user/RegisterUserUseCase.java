package pl.dominik.elearningcenter.application.user;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.command.RegisterUserCommand;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;


@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public Long execute(RegisterUserCommand command){
        Email email = new Email(command.email());
        Username username = new Username(command.username());

        if (userRepository.existsByEmail(email)){
            throw new DomainException("Email already exists " + command.email());
        }
        if (userRepository.existsByUsername(username)){
            throw new DomainException("Username already exists " + command.username());
        }
        Password password = Password.fromRaw(command.password());

        User user = User.register(username, email, password, command.role());

        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}
