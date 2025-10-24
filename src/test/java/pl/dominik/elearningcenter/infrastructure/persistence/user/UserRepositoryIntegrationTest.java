package pl.dominik.elearningcenter.infrastructure.persistence.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRole;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(UserRepositoryAdapter.class)
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepositoryAdapter userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );

        userRepository.save(user);
        Optional<User> retrieved = userRepository.findById(user.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getUsername().getValue()).isEqualTo("john_doe");
        assertThat(retrieved.get().getEmail().getValue()).isEqualTo("john@example.com");
        assertThat(retrieved.get().getRole()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = User.register(
                new Username("jane_doe"),
                new Email("jane@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.INSTRUCTOR
        );
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(new Email("jane@example.com"));

        assertThat(found).isPresent();
        assertThat(found.get().getUsername().getValue()).isEqualTo("jane_doe");
    }

    @Test
    void shouldFindUserByUsername() {
        User user = User.register(
                new Username("admin_user"),
                new Email("admin@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.ADMIN
        );
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername(new Username("admin_user"));

        assertThat(found).isPresent();
        assertThat(found.get().getEmail().getValue()).isEqualTo("admin@example.com");
    }

    @Test
    void shouldCheckIfEmailExists() {
        User user = User.register(
                new Username("test_user"),
                new Email("test@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        userRepository.save(user);

        assertThat(userRepository.existsByEmail(new Email("test@example.com"))).isTrue();
        assertThat(userRepository.existsByEmail(new Email("nonexistent@example.com"))).isFalse();
    }

    @Test
    void shouldCheckIfUsernameExists() {
        User user = User.register(
                new Username("existing_user"),
                new Email("user@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        userRepository.save(user);

        assertThat(userRepository.existsByUsername(new Username("existing_user"))).isTrue();
        assertThat(userRepository.existsByUsername(new Username("nonexistent_user"))).isFalse();
    }

    @Test
    void shouldFindAllUsersWithPagination() {
        userRepository.save(User.register(
                new Username("user1"),
                new Email("user1@example.com"),
                Password.fromHashed("pass"),
                UserRole.STUDENT
        ));
        userRepository.save(User.register(
                new Username("user2"),
                new Email("user2@example.com"),
                Password.fromHashed("pass"),
                UserRole.STUDENT
        ));
        userRepository.save(User.register(
                new Username("user3"),
                new Email("user3@example.com"),
                Password.fromHashed("pass"),
                UserRole.INSTRUCTOR
        ));

        Page<User> page = userRepository.findAll(PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void shouldDeleteUser() {
        User user = User.register(
                new Username("to_delete"),
                new Email("delete@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        userRepository.save(user);
        Long userId = user.getId();

        userRepository.delete(user);

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        assertThatThrownBy(() -> userRepository.findByIdOrThrow(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found: 999");
    }

    @Test
    void shouldUpdateUserEmail() {
        User user = User.register(
                new Username("update_test"),
                new Email("old@example.com"),
                Password.fromHashed("hashedPassword"),
                UserRole.STUDENT
        );
        userRepository.save(user);

        user.updateEmail(new Email("new@example.com"));
        userRepository.save(user);

        User updated = userRepository.findById(user.getId()).get();
        assertThat(updated.getEmail().getValue()).isEqualTo("new@example.com");
    }
}
