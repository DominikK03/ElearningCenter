package pl.dominik.elearningcenter.application.wallet.mapper;

import org.springframework.stereotype.Component;
import pl.dominik.elearningcenter.application.wallet.dto.WalletTransactionDTO;
import pl.dominik.elearningcenter.domain.wallet.WalletTransaction;

@Component
public class WalletTransactionMapper {

    public WalletTransactionDTO toDto(WalletTransaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        return new WalletTransactionDTO(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getReferenceId(),
                transaction.getCreatedAt()
        );
    }
}
