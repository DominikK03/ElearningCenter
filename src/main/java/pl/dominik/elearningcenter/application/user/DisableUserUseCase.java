package pl.dominik.elearningcenter.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

@Service
public class DisableUserUseCase {
    private final UserRepository userRepository;

    public DisableUserUseCase(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.disable();
    }
}
