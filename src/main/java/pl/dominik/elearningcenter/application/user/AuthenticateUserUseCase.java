package pl.dominik.elearningcenter.application.user;


import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.dto.AuthenticateUserCommand;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

@Service
@Transactional
public class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO execute(AuthenticateUserCommand command){
        Email email = new Email(command.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), user.getPassword().getValue())){
            throw new DomainException("Invalid credentials");
        }
        if (!user.isEnabled()){
            throw new DomainException("User account is disabled");
        }

        return UserDTO.from(user);
    }
}
