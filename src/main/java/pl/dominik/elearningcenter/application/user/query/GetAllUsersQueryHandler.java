package pl.dominik.elearningcenter.application.user.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.dto.PagedUsersDTO;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.application.user.mapper.UserMapper;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

import java.util.List;

@Service
public class GetAllUsersQueryHandler {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetAllUsersQueryHandler(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public PagedUsersDTO handle(GetAllUsersQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDTO> users = userPage.getContent()
                .stream()
                .map(userMapper::toDto)
                .toList();

        return new PagedUsersDTO(
                users,
                userPage.getNumber(),
                userPage.getTotalPages(),
                userPage.getTotalElements()
        );
    }
}
