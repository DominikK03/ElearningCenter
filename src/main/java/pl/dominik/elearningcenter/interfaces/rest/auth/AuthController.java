package pl.dominik.elearningcenter.interfaces.rest.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dominik.elearningcenter.application.user.AuthenticateUserUseCase;
import pl.dominik.elearningcenter.application.user.command.AuthenticateUserInput;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.infrastructure.security.JwtTokenProvider;
import pl.dominik.elearningcenter.infrastructure.security.dto.JwtAuthenticationResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.request.LoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, JwtTokenProvider jwtTokenProvider) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateUserInput command = new AuthenticateUserInput(
                request.email(),
                request.password()
        );

        UserDTO user = authenticateUserUseCase.execute(command);

        String token = jwtTokenProvider.generateToken(
                user.id(),
                user.email(),
                user.role()
        );

        JwtAuthenticationResponse response = JwtAuthenticationResponse.create(
                token,
                user,
                jwtTokenProvider.getExpirationTime()
        );

        return ResponseEntity.ok(response);
    }

}
