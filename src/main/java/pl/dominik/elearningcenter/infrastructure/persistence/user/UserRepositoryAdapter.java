package pl.dominik.elearningcenter.infrastructure.persistence.user;

import org.springframework.stereotype.Repository;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return jpaRepository.findByUsername(username.getValue());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue());
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsByUsername(Username username) {
        return jpaRepository.existsByUsername(username.getValue());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public void delete(User user) {
        jpaRepository.delete(user);
    }
}
