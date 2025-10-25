package pl.dominik.elearningcenter.application.user.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.user.User;

import java.util.List;

@Component
public class UserMapper {

    public UserDTO toDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return new UserDTO(
                user.getId(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                user.getRole(),
                user.getCreatedAt(),
                user.isEnabled(),
                user.isEmailVerified(),
                user.getBalance().getAmount().doubleValue()
        );
    }

    public List<UserDTO> toDtoList(List<User> users) {
        if (users == null) {
            throw new IllegalArgumentException("Users list cannot be null");
        }

        return users.stream()
                .map(this::toDto)
                .toList();
    }
}
