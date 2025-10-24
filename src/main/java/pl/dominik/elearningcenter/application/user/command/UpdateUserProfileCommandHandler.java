package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

@Service
public class UpdateUserProfileCommandHandler {
    private final UserRepository userRepository;

    public UpdateUserProfileCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void handle(UpdateUserProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (command.newEmail() != null) {
            Email newEmail = new Email(command.newEmail());
            if (!newEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new DomainException("Email is already taken");
                }
                user.updateEmail(newEmail);
            }
        }
        if (command.newUsername() != null) {
            Username newUsername = new Username(command.newUsername());
            if (!newUsername.equals(user.getUsername())) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new DomainException("Username is already taken");
                }
                user.updateUsername(newUsername);
            }
        }
    }
}
