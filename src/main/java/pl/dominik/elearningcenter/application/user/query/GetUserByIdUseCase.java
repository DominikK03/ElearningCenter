package pl.dominik.elearningcenter.application.user.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

@Service
public class GetUserByIdUseCase {
    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserDTO execute(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return UserDTO.from(user);
    }
}
