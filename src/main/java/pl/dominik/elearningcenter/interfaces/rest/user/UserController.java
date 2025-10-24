package pl.dominik.elearningcenter.interfaces.rest.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.user.command.*;
import pl.dominik.elearningcenter.application.user.query.*;
import pl.dominik.elearningcenter.application.user.dto.PagedUsersDTO;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.application.user.mapper.UserMapper;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.request.ChangePasswordRequest;
import pl.dominik.elearningcenter.interfaces.rest.user.request.LoginRequest;
import pl.dominik.elearningcenter.interfaces.rest.user.request.RegisterUserRequest;
import pl.dominik.elearningcenter.interfaces.rest.user.request.UpdateUserProfileRequest;
import pl.dominik.elearningcenter.interfaces.rest.user.response.PagedUsersResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.response.UserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUserCommandHandler registerUserCommandHandler;
    private final AuthenticateUserQueryHandler authenticateUserQueryHandler;
    private final GetUserByIdQueryHandler getUserByIdQueryHandler;
    private final GetAllUsersQueryHandler getAllUsersQueryHandler;
    private final UpdateUserProfileCommandHandler updateUserProfileCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final EnableUserCommandHandler enableUserCommandHandler;
    private final DisableUserCommandHandler disableUserCommandHandler;
    private final UserMapper userMapper;

    public UserController(
            RegisterUserCommandHandler registerUserCommandHandler,
            AuthenticateUserQueryHandler authenticateUserQueryHandler,
            GetUserByIdQueryHandler getUserByIdQueryHandler,
            GetAllUsersQueryHandler getAllUsersQueryHandler,
            UpdateUserProfileCommandHandler updateUserProfileCommandHandler,
            ChangePasswordCommandHandler changePasswordCommandHandler,
            EnableUserCommandHandler enableUserCommandHandler,
            DisableUserCommandHandler disableUserCommandHandler,
            UserMapper userMapper
    ) {
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.authenticateUserQueryHandler = authenticateUserQueryHandler;
        this.getUserByIdQueryHandler = getUserByIdQueryHandler;
        this.getAllUsersQueryHandler = getAllUsersQueryHandler;
        this.updateUserProfileCommandHandler = updateUserProfileCommandHandler;
        this.changePasswordCommandHandler = changePasswordCommandHandler;
        this.enableUserCommandHandler = enableUserCommandHandler;
        this.disableUserCommandHandler = disableUserCommandHandler;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AckResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.role()
        );

        Long userId = registerUserCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(userId, "User"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthenticateUserQuery query = new AuthenticateUserQuery(
                request.email(),
                request.password()
        );
        User user = authenticateUserQueryHandler.handle(query);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        UserDTO userDTO = userMapper.toDto(user);
        UserResponse response = UserResponse.from(userDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<AckResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(AckResponse.success("Logged out successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser){
        if (!currentUser.getUserId().equals(id)){
            throw new DomainException("Permission denied. You can only see your own profile");
        }
        GetUserByIdQuery query = new GetUserByIdQuery(id);
        UserDTO userDTO = getUserByIdQueryHandler.handle(query);
        return ResponseEntity.ok(UserResponse.from(userDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedUsersResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllUsersQuery query = new GetAllUsersQuery(page, size);
        PagedUsersDTO dto = getAllUsersQueryHandler.handle(query);
        return ResponseEntity.ok(PagedUsersResponse.from(dto));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<AckResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
            ){
        if (!currentUser.getUserId().equals(id)){
            throw new DomainException("Permission denied. Cannot update others profile");
        }
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                id,
                request.email(),
                request.username()
        );
        updateUserProfileCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.updated("User profile"));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<AckResponse> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
            ) {
        if (!currentUser.getUserId().equals(id)){
            throw new DomainException("Permission denied. You can only change your own password");
        }
        ChangePasswordCommand command = new ChangePasswordCommand(
                id,
                request.oldPassword(),
                request.newPassword()
        );
        changePasswordCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Password changed Successfully"));
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AckResponse> enableUser(@PathVariable Long id){
        EnableUserCommand command = new EnableUserCommand(id);
        enableUserCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("User account enabled"));
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AckResponse> disableUser(@PathVariable Long id) {
        DisableUserCommand command = new DisableUserCommand(id);
        disableUserCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("User account disabled"));
    }
}
