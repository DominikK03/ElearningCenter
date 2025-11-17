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
import pl.dominik.elearningcenter.application.wallet.query.GetUserTransactionsQuery;
import pl.dominik.elearningcenter.application.wallet.query.GetUserTransactionsQueryHandler;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.infrastructure.security.JwtTokenProvider;
import pl.dominik.elearningcenter.interfaces.rest.auth.AuthenticationResponse;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.request.*;
import pl.dominik.elearningcenter.interfaces.rest.user.response.PagedUsersResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.response.PagedWalletTransactionsResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.response.UserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUserCommandHandler registerUserCommandHandler;
    private final AuthenticateUserQueryHandler authenticateUserQueryHandler;
    private final GetUserByIdQueryHandler getUserByIdQueryHandler;
    private final GetCurrentUserQueryHandler getCurrentUserQueryHandler;
    private final GetAllUsersQueryHandler getAllUsersQueryHandler;
    private final UpdateUserProfileCommandHandler updateUserProfileCommandHandler;
    private final ChangePasswordCommandHandler changePasswordCommandHandler;
    private final EnableUserCommandHandler enableUserCommandHandler;
    private final DisableUserCommandHandler disableUserCommandHandler;
    private final VerifyEmailCommandHandler verifyEmailCommandHandler;
    private final ResendVerificationEmailCommandHandler resendVerificationEmailCommandHandler;
    private final RequestPasswordResetCommandHandler requestPasswordResetCommandHandler;
    private final ResetPasswordCommandHandler resetPasswordCommandHandler;
    private final AddBalanceCommandHandler addBalanceCommandHandler;
    private final GetUserTransactionsQueryHandler getUserTransactionsQueryHandler;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(
            RegisterUserCommandHandler registerUserCommandHandler,
            AuthenticateUserQueryHandler authenticateUserQueryHandler,
            GetUserByIdQueryHandler getUserByIdQueryHandler,
            GetCurrentUserQueryHandler getCurrentUserQueryHandler,
            GetAllUsersQueryHandler getAllUsersQueryHandler,
            UpdateUserProfileCommandHandler updateUserProfileCommandHandler,
            ChangePasswordCommandHandler changePasswordCommandHandler,
            EnableUserCommandHandler enableUserCommandHandler,
            DisableUserCommandHandler disableUserCommandHandler,
            VerifyEmailCommandHandler verifyEmailCommandHandler,
            ResendVerificationEmailCommandHandler resendVerificationEmailCommandHandler,
            RequestPasswordResetCommandHandler requestPasswordResetCommandHandler,
            ResetPasswordCommandHandler resetPasswordCommandHandler,
            AddBalanceCommandHandler addBalanceCommandHandler,
            GetUserTransactionsQueryHandler getUserTransactionsQueryHandler,
            UserMapper userMapper,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.registerUserCommandHandler = registerUserCommandHandler;
        this.authenticateUserQueryHandler = authenticateUserQueryHandler;
        this.getUserByIdQueryHandler = getUserByIdQueryHandler;
        this.getCurrentUserQueryHandler = getCurrentUserQueryHandler;
        this.getAllUsersQueryHandler = getAllUsersQueryHandler;
        this.updateUserProfileCommandHandler = updateUserProfileCommandHandler;
        this.changePasswordCommandHandler = changePasswordCommandHandler;
        this.enableUserCommandHandler = enableUserCommandHandler;
        this.disableUserCommandHandler = disableUserCommandHandler;
        this.verifyEmailCommandHandler = verifyEmailCommandHandler;
        this.resendVerificationEmailCommandHandler = resendVerificationEmailCommandHandler;
        this.requestPasswordResetCommandHandler = requestPasswordResetCommandHandler;
        this.resetPasswordCommandHandler = resetPasswordCommandHandler;
        this.addBalanceCommandHandler = addBalanceCommandHandler;
        this.getUserTransactionsQueryHandler = getUserTransactionsQueryHandler;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
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
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
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

        // Generate JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .userId(user.getId())
                .email(user.getEmail().getValue())
                .firstName(user.getUsername().getValue())
                .lastName("")
                .role(user.getRole().name())
                .build());
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

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        GetCurrentUserQuery query = new GetCurrentUserQuery(currentUser.getUserId());
        UserDTO userDTO = getCurrentUserQueryHandler.handle(query);
        return ResponseEntity.ok(UserResponse.from(userDTO));
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

    @PostMapping("/verify-email")
    public ResponseEntity<AckResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        VerifyEmailCommand command = new VerifyEmailCommand(request.token());
        verifyEmailCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Email verified successfully"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<AckResponse> resendVerificationEmail(@Valid @RequestBody ResendVerificationEmailRequest request) {
        ResendVerificationEmailCommand command = new ResendVerificationEmailCommand(request.email());
        resendVerificationEmailCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Verification email resent successfully"));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<AckResponse> requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequest request) {
        RequestPasswordResetCommand command = new RequestPasswordResetCommand(request.email());
        requestPasswordResetCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Password reset email sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AckResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordCommand command = new ResetPasswordCommand(request.token(), request.newPassword());
        resetPasswordCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Password reset successfully"));
    }

    @PostMapping("/{id}/balance/add")
    public ResponseEntity<AckResponse> addBalance(
            @PathVariable Long id,
            @Valid @RequestBody AddBalanceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        if (!currentUser.getUserId().equals(id)) {
            throw new DomainException("Permission denied. You can only add balance to your own account");
        }
        AddBalanceCommand command = new AddBalanceCommand(id, request.amount());
        addBalanceCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Balance added successfully"));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<PagedWalletTransactionsResponse> getTransactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!currentUser.getUserId().equals(id) && !isAdmin) {
            throw new DomainException("Permission denied. You can only view your own transactions");
        }

        GetUserTransactionsQuery query = new GetUserTransactionsQuery(id, page, size);
        return ResponseEntity.ok(
                PagedWalletTransactionsResponse.from(getUserTransactionsQueryHandler.handle(query))
        );
    }
}
