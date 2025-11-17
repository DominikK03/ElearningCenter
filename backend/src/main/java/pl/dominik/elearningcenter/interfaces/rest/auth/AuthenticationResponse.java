package pl.dominik.elearningcenter.interfaces.rest.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // in milliseconds
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
