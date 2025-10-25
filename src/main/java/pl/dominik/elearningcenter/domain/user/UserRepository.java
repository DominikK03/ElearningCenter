package pl.dominik.elearningcenter.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.exception.UserNotFoundException;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);
    Page<User> findAll(Pageable pageable);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    void delete(User user);
    default User findByIdOrThrow(Long userId){
        return findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
}
