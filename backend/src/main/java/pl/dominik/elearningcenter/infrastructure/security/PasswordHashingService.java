package pl.dominik.elearningcenter.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;

@Component
public class PasswordHashingService {
    private static final int MIN_LENGTH = 6;
    private final PasswordEncoder passwordEncoder;

    public PasswordHashingService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Password hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        return Password.fromHashed(hashed);
    }

    public boolean matches(String rawPassword, Password hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword.getValue());
    }
}
