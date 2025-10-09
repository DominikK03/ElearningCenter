package pl.dominik.elearningcenter.application.user;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.dto.RegisterUserCommand;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;


@Service
@Transactional
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO execute(RegisterUserCommand command){
        Email email = new Email(command.email());
        Username username = new Username(command.username());

        if (userRepository.existsByEmail(email)){
            throw new DomainException("Email already exists " + command.email());
        }
        if (userRepository.existsByUsername(username)){
            throw new DomainException("Username already exists " + command.username());
        }
        String hashedPassword = passwordEncoder.encode(command.password());
        Password password = Password.fromHashed(hashedPassword);

        User user = User.register(username, email, password, command.role());

        User savedUser = userRepository.save(user);

        return UserDTO.from(savedUser);
    }
}
