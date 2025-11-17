package pl.dominik.elearningcenter.domain.wallet;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.math.RoundingMode;

@Entity
@Table(
        name = "wallet_transactions",
        indexes = {
                @Index(name = "idx_wallet_tx_user", columnList = "user_id"),
                @Index(name = "idx_wallet_tx_created_at", columnList = "created_at")
        }
)
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private TransactionType type;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected WalletTransaction() {}

    private WalletTransaction(Long userId,
                              BigDecimal amount,
                              String currency,
                              TransactionType type,
                              String description,
                              Long referenceId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.userId = userId;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
        this.type = type;
        this.description = description;
        this.referenceId = referenceId;
        this.createdAt = LocalDateTime.now();
    }

    public static WalletTransaction credit(Long userId,
                                           BigDecimal amount,
                                           String currency,
                                           String description,
                                           Long referenceId) {
        return new WalletTransaction(userId, amount, currency, TransactionType.CREDIT, description, referenceId);
    }

    public static WalletTransaction debit(Long userId,
                                          BigDecimal amount,
                                          String currency,
                                          String description,
                                          Long referenceId) {
        return new WalletTransaction(userId, amount, currency, TransactionType.DEBIT, description, referenceId);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WalletTransaction that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
