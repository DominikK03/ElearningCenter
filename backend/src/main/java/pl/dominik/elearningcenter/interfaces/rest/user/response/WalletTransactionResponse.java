package pl.dominik.elearningcenter.interfaces.rest.user.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import pl.dominik.elearningcenter.application.wallet.dto.WalletTransactionDTO;
import pl.dominik.elearningcenter.domain.wallet.TransactionType;

public record WalletTransactionResponse(
        Long id,
        BigDecimal amount,
        String currency,
        TransactionType type,
        String description,
        Long referenceId,
        LocalDateTime createdAt
) {
    public static WalletTransactionResponse from(WalletTransactionDTO dto) {
        return new WalletTransactionResponse(
                dto.id(),
                dto.amount(),
                dto.currency(),
                dto.type(),
                dto.description(),
                dto.referenceId(),
                dto.createdAt()
        );
    }
}
