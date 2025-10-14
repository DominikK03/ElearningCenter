package pl.dominik.elearningcenter.application.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.dominik.elearningcenter.application.user.command.GetAllUsersInput;
import pl.dominik.elearningcenter.application.user.dto.PagedUsersDTO;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

import java.util.List;

@Service
public class GetAllUsersUseCase {
    private final UserRepository userRepository;

    public GetAllUsersUseCase(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public PagedUsersDTO execute(GetAllUsersInput command){
        Pageable pageable = PageRequest.of(command.page(), command.size());
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDTO> users = userPage.getContent()
                .stream()
                .map(UserDTO::from)
                .toList();

        return new PagedUsersDTO(
                users,
                userPage.getNumber(),
                userPage.getTotalPages(),
                userPage.getTotalElements()
        );
    }
}
