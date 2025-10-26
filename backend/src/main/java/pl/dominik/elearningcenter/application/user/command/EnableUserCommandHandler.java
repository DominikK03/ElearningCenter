package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

@Service
public class EnableUserCommandHandler {
    private final UserRepository userRepository;

    public EnableUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void handle(EnableUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.enable();
    }
}
