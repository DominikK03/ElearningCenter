package pl.dominik.elearningcenter.domain.user;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User extends AggregateRoot<Long> {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "username", unique = true, nullable = false, length = 50))
    private Username username;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private Password password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean enabled = false;

    protected User() {
        super();
    }

    private User(Username username, Email email, Password password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.enabled = false;
    }

    public static User register(Username username, Email email, Password rawPassword, UserRole userRole) {
        return new User(username, email, rawPassword, userRole);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void changePassword(Password newPassword) {
        if (newPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        this.password = newPassword;
    }
    public boolean isEnabled(){
        return enabled;
    }
    public boolean hasRole(UserRole role){
        return this.role == role;
    }
    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
