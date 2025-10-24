package pl.dominik.elearningcenter.domain.user;

import org.junit.jupiter.api.Test;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void shouldRegisterUserSuccessfully() {
        Username username = new Username("john_doe");
        Email email = new Email("john@example.com");
        Password password = Password.fromHashed("hashedPassword123");

        User user = User.register(username, email, password, UserRole.STUDENT);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(user.isEnabled()).isFalse();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldEnableUser() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );

        user.enable();

        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void shouldDisableUser() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        user.enable();

        user.disable();

        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("oldHashedPassword"),
                UserRole.STUDENT
        );
        Password newPassword = Password.fromHashed("newHashedPassword");

        user.changePassword(newPassword, true);

        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldThrowExceptionWhenOldPasswordDoesNotMatch() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("oldHashedPassword"),
                UserRole.STUDENT
        );
        Password newPassword = Password.fromHashed("newHashedPassword");

        assertThatThrownBy(() -> user.changePassword(newPassword, false))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid old password");
    }

    @Test
    void shouldUpdateEmailSuccessfully() {
        User user = User.register(
                new Username("john_doe"),
                new Email("old@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        Email newEmail = new Email("new@example.com");

        user.updateEmail(newEmail);

        assertThat(user.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEmailToNull() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );

        assertThatThrownBy(() -> user.updateEmail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email cannot be null");
    }

    @Test
    void shouldUpdateUsernameSuccessfully() {
        User user = User.register(
                new Username("old_username"),
                new Email("john@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        Username newUsername = new Username("new_username");

        user.updateUsername(newUsername);

        assertThat(user.getUsername()).isEqualTo(newUsername);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUsernameToNull() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );

        assertThatThrownBy(() -> user.updateUsername(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username cannot be null");
    }

    @Test
    void shouldCheckIfUserHasRole() {
        User student = User.register(
                new Username("student"),
                new Email("student@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );

        assertThat(student.hasRole(UserRole.STUDENT)).isTrue();
        assertThat(student.hasRole(UserRole.INSTRUCTOR)).isFalse();
        assertThat(student.hasRole(UserRole.ADMIN)).isFalse();
    }
}
