package pl.dominik.elearningcenter.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dominik.elearningcenter.domain.user.User;

import java.util.Optional;

interface UserJpaRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username.value = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email.value = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.verificationToken = :token")
    Optional<User> findByVerificationToken(@Param("token") String token);

    @Query("SELECT u FROM User u WHERE u.passwordResetToken = :token")
    Optional<User> findByPasswordResetToken(@Param("token") String token);

    @Query("SELECT COUNT(u) > 0 FROM User u where  u.username.value = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("select count(u) > 0 from User u where u.email.value = :email")
    boolean existsByEmail(@Param("email") String email);

}
