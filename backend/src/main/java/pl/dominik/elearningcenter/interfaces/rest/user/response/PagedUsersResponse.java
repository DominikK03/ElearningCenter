package pl.dominik.elearningcenter.interfaces.rest.user.response;

import pl.dominik.elearningcenter.application.user.dto.PagedUsersDTO;

import java.util.List;

public record PagedUsersResponse(
        List<UserResponse> users,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static PagedUsersResponse from(PagedUsersDTO dto){
        List<UserResponse> users = dto.users().stream()
                .map(UserResponse::from)
                .toList();

        return new PagedUsersResponse(
                users,
                dto.currentPage(),
                dto.totalPages(),
                dto.totalElements()
        );
    }
}
