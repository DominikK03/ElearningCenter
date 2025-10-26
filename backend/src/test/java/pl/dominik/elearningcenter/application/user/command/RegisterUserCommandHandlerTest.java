package pl.dominik.elearningcenter.application.user.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.UserRole;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHashingService passwordHashingService;

    @InjectMocks
    private RegisterUserCommandHandler handler;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "john_doe",
                "john@example.com",
                "password123",
                UserRole.STUDENT
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(passwordHashingService.hashPassword("password123"))
                .thenReturn(Password.fromHashed("hashedPassword123"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Symulacja ID przypisanego przez bazę
            return user;
        });

        Long userId = handler.handle(command);

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).existsByUsername(any(Username.class));
        verify(passwordHashingService).hashPassword("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterUserCommand command = new RegisterUserCommand(
                "john_doe",
                "existing@example.com",
                "password123",
                UserRole.STUDENT
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterUserCommand command = new RegisterUserCommand(
                "existing_user",
                "john@example.com",
                "password123",
                UserRole.STUDENT
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Username already exists");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldHashPasswordBeforeSaving() {
        RegisterUserCommand command = new RegisterUserCommand(
                "john_doe",
                "john@example.com",
                "plainPassword",
                UserRole.STUDENT
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(passwordHashingService.hashPassword("plainPassword"))
                .thenReturn(Password.fromHashed("$2a$10$hashedPassword"));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        handler.handle(command);

        verify(passwordHashingService).hashPassword("plainPassword");
    }
}
