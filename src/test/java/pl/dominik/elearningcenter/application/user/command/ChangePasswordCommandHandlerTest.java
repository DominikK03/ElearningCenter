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
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHashingService passwordHashingService;

    @InjectMocks
    private ChangePasswordCommandHandler handler;

    @Test
    void shouldChangePasswordSuccessfully() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("oldHashedPassword"),
                UserRole.STUDENT
        );
        ChangePasswordCommand command = new ChangePasswordCommand(1L, "oldPassword", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordHashingService.matches(eq("oldPassword"), any(Password.class))).thenReturn(true);
        when(passwordHashingService.hashPassword("newPassword123"))
                .thenReturn(Password.fromHashed("newHashedPassword123"));

        handler.handle(command);

        verify(userRepository).findById(1L);
        verify(passwordHashingService).matches(eq("oldPassword"), any(Password.class));
        verify(passwordHashingService).hashPassword("newPassword123");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        ChangePasswordCommand command = new ChangePasswordCommand(999L, "oldPassword", "newPassword");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(999L);
        verify(passwordHashingService, never()).matches(any(), any());
        verify(passwordHashingService, never()).hashPassword(any());
    }

    @Test
    void shouldThrowExceptionWhenOldPasswordIsInvalid() {
        User user = User.register(
                new Username("john_doe"),
                new Email("john@example.com"),
                Password.fromHashed("oldHashedPassword"),
                UserRole.STUDENT
        );
        ChangePasswordCommand command = new ChangePasswordCommand(1L, "wrongOldPassword", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordHashingService.matches(eq("wrongOldPassword"), any(Password.class))).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid old password");

        verify(userRepository).findById(1L);
        verify(passwordHashingService).matches(eq("wrongOldPassword"), any(Password.class));
        verify(passwordHashingService, never()).hashPassword(any());
    }
}
