package pl.dominik.elearningcenter.domain.shared.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public final class Password {
    private static final int MIN_LENGHT = 6;

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
        if (rawPassword.length() < MIN_LENGHT){
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGHT + "characters");
        }
        return new Password(rawPassword);
    }
    public static Password fromHashed(String hashedPassword){
        return new Password(hashedPassword);
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
