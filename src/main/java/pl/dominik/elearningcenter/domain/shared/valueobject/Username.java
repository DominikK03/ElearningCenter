package pl.dominik.elearningcenter.domain.shared.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public final class Username {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    private String value;

    protected Username(){}

    public Username(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Username must be between 3-50 characters long and contain " +
                    "only letters, numbers, underscore or hyphen ");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    public int hashCode() {
        return Objects.hash(value);
    }

    public String toString() {
        return value;
    }
}


