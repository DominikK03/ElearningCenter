package pl.dominik.elearningcenter.application.user.query;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

@Service
@Transactional
public class AuthenticateUserQueryHandler {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public AuthenticateUserQueryHandler(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    public User handle(AuthenticateUserQuery query) {
        Email email = new Email(query.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("Invalid credentials"));

        if (!passwordHashingService.matches(query.password(), user.getPassword())) {
            throw new DomainException("Invalid credentials");
        }
        if (!user.isEnabled()) {
            throw new DomainException("User account is disabled");
        }

        return user;
    }
}
