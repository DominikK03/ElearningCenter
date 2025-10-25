package pl.dominik.elearningcenter.domain.user;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User extends AggregateRoot<Long> {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "username", unique = true, nullable = false, length = 50))
    private Username username;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private Password password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expires_at")
    private LocalDateTime verificationTokenExpiresAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expires_at")
    private LocalDateTime passwordResetTokenExpiresAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount", nullable = false, precision = 10, scale = 2)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "balance_currency", nullable = false, length = 3))
    })
    private Money balance;

    protected User() {
        super();
    }

    private User(Username username, Email email, Password password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.enabled = false;
        this.balance = Money.pln(BigDecimal.ZERO);
    }

    public static User register(Username username, Email email, Password rawPassword, UserRole userRole) {
        return new User(username, email, rawPassword, userRole);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void changePassword(Password newHashedPassword, boolean oldPasswordMatches) {
        if (!oldPasswordMatches) {
            throw new DomainException("Invalid old password");
        }
        this.password = newHashedPassword;
    }

    public void updateEmail(Email newEmail){
        if (newEmail == null){
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }

    public void updateUsername(Username newUsername){
        if (newUsername == null){
            throw new IllegalArgumentException("Username cannot be null");
        }
        this.username = newUsername;
    }
    public boolean isEnabled(){
        return enabled;
    }
    public boolean hasRole(UserRole role){
        return this.role == role;
    }
    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Money getBalance() {
        return balance;
    }

    public void addBalance(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.balance = this.balance.add(amount);
    }

    public void deductBalance(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (this.balance.getAmount().compareTo(amount.getAmount()) < 0) {
            throw new DomainException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public boolean hasEnoughBalance(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (!this.balance.getCurrencyCode().equals(amount.getCurrencyCode())) {
            throw new IllegalArgumentException("Cannot compare balance with different currencies");
        }
        return this.balance.getAmount().compareTo(amount.getAmount()) >= 0;
    }

    public void generateVerificationToken(String token, int expirationHours) {
        this.verificationToken = token;
        this.verificationTokenExpiresAt = LocalDateTime.now().plusHours(expirationHours);
    }

    public void verifyEmail(String token) {
        if (this.verificationToken == null) {
            throw new DomainException("No verification token found");
        }
        if (!this.verificationToken.equals(token)) {
            throw new DomainException("Invalid verification token");
        }
        if (LocalDateTime.now().isAfter(this.verificationTokenExpiresAt)) {
            throw new DomainException("Verification token has expired");
        }
        this.emailVerified = true;
        this.verificationToken = null;
        this.verificationTokenExpiresAt = null;
    }

    public void generatePasswordResetToken(String token, int expirationHours) {
        this.passwordResetToken = token;
        this.passwordResetTokenExpiresAt = LocalDateTime.now().plusHours(expirationHours);
    }

    public void resetPassword(String token, Password newHashedPassword) {
        if (this.passwordResetToken == null) {
            throw new DomainException("No password reset token found");
        }
        if (!this.passwordResetToken.equals(token)) {
            throw new DomainException("Invalid password reset token");
        }
        if (LocalDateTime.now().isAfter(this.passwordResetTokenExpiresAt)) {
            throw new DomainException("Password reset token has expired");
        }
        this.password = newHashedPassword;
        this.passwordResetToken = null;
        this.passwordResetTokenExpiresAt = null;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
