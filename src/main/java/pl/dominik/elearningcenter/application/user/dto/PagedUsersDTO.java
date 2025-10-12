package pl.dominik.elearningcenter.application.user.dto;

import java.util.List;

public record PagedUsersDTO(
        List<UserDTO> users,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
