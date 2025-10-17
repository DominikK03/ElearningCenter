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
import pl.dominik.elearningcenter.application.user.input.*;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
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
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final UserRepository userRepository;

    public UserController(
            RegisterUserUseCase registerUserUseCase,
            AuthenticateUserUseCase authenticateUserUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            GetAllUsersUseCase getAllUsersUseCase,
            UpdateUserProfileUseCase updateUserProfileUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            EnableUserUseCase enableUserUseCase,
            DisableUserUseCase disableUserUseCase,
            UserRepository userRepository
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.enableUserUseCase = enableUserUseCase;
        this.disableUserUseCase = disableUserUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AckResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserInput command = new RegisterUserInput(
                request.username(),
                request.email(),
                request.password(),
                request.role()
        );

        Long userId = registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(userId, "User"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthenticateUserInput command = new AuthenticateUserInput(
                request.email(),
                request.password()
        );
        UserDTO userDTO = authenticateUserUseCase.execute(command);

        Email email = new Email(userDTO.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("User not found"));

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
        UserDTO userDTO = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserResponse.from(userDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedUsersResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllUsersInput command = new GetAllUsersInput(page,size);
        PagedUsersDTO dto = getAllUsersUseCase.execute(command);
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
        UpdateUserProfileInput command = new UpdateUserProfileInput(
                id,
                request.email(),
                request.username()
        );
        updateUserProfileUseCase.execute(command);
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
        ChangePasswordInput command = new ChangePasswordInput(
                id,
                request.oldPassword(),
                request.newPassword()
        );
        changePasswordUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Password changed Successfully"));
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AckResponse> enableUser(@PathVariable Long id){
        enableUserUseCase.execute(id);
        return ResponseEntity.ok(AckResponse.success("User account enabled"));
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AckResponse> disableUser(@PathVariable Long id) {
        disableUserUseCase.execute(id);
        return ResponseEntity.ok(AckResponse.success("User account disabled"));
    }
}
