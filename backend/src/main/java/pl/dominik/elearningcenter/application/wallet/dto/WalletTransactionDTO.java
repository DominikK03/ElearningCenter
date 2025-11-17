package pl.dominik.elearningcenter.application.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import pl.dominik.elearningcenter.domain.wallet.TransactionType;

public record WalletTransactionDTO(
        Long id,
        Long userId,
        BigDecimal amount,
        String currency,
        TransactionType type,
        String description,
        Long referenceId,
        LocalDateTime createdAt
) {
}
