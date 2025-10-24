package pl.dominik.elearningcenter.application.user.query;

import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.application.user.mapper.UserMapper;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

@Service
public class GetUserByIdQueryHandler {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetUserByIdQueryHandler(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO handle(GetUserByIdQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + query.userId()));
        return userMapper.toDto(user);
    }
}
