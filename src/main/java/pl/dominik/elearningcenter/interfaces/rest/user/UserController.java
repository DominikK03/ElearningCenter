package pl.dominik.elearningcenter.interfaces.rest.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dominik.elearningcenter.application.user.AuthenticateUserUseCase;
import pl.dominik.elearningcenter.application.user.RegisterUserUseCase;
import pl.dominik.elearningcenter.application.user.command.AuthenticateUserCommand;
import pl.dominik.elearningcenter.application.user.command.RegisterUserCommand;
import pl.dominik.elearningcenter.application.user.dto.UserDTO;
import pl.dominik.elearningcenter.interfaces.rest.user.dto.LoginRequest;
import pl.dominik.elearningcenter.interfaces.rest.user.dto.RegisterUserRequest;
import pl.dominik.elearningcenter.interfaces.rest.user.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase, AuthenticateUserUseCase authenticateUserUseCase){
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request){
        RegisterUserCommand command = new RegisterUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.role()
        );

        UserDTO userDTO = registerUserUseCase.execute(command);

        UserResponse response = UserResponse.from(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request){
        AuthenticateUserCommand command = new AuthenticateUserCommand(
                request.email(),
                request.password()
        );

        UserDTO userDTO = authenticateUserUseCase.execute(command);

        UserResponse response = UserResponse.from(userDTO);

        return ResponseEntity.ok(response);
    }
}
