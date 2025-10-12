package pl.dominik.elearningcenter.domain.shared.valueobject;

import jakarta.persistence.Embeddable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Embeddable
public final class Password {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private static final int MIN_LENGTH = 6;

    private String value;

    protected Password() {
    }

    private Password(String value){
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        this.value = value;
    }
    public static Password fromRaw(String rawPassword){
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH){
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + "characters");
        }
        String hashed = encoder.encode(rawPassword);
        return new Password(hashed);
    }
    public static Password fromHashed(String hashedPassword){
        return new Password(hashedPassword);
    }

    public boolean matches(String rawPassword){
        return encoder.matches(rawPassword, this.value);
    }

    public String getValue(){
        return value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


}
