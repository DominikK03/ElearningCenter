package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.user.input.ChangePasswordInput;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

@Service
public class ChangePasswordUseCase {
    private final UserRepository userRepository;

    public ChangePasswordUseCase(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(ChangePasswordInput command){
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.changePassword(command.oldPassword(), command.newPassword());
    }
}
