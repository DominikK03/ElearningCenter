package pl.dominik.elearningcenter.interfaces.rest.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.infrastructure.security.JwtTokenProvider;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Invalid refresh token provided");
            return ResponseEntity.status(401).build();
        }

        // Check if it's actually a refresh token
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            log.warn("Access token provided instead of refresh token");
            return ResponseEntity.status(401).build();
        }

        // Get username from refresh token
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Find user
        User user = userRepository.findByEmail(new Email(username))
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        // Check if user is enabled
        if (!user.isEnabled()) {
            log.warn("Disabled user attempted to refresh token: {}", username);
            return ResponseEntity.status(401).build();
        }

        // Create authentication object
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        log.info("Token refreshed for user: {}", username);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .userId(user.getId())
                .email(user.getEmail().getValue())
                .firstName(user.getUsername().getValue())
                .lastName("")
                .role(user.getRole().name())
                .build());
    }
}
