package pl.dominik.elearningcenter.domain.user;

import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    List<User> findAll();
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    void delete(User user);
}
