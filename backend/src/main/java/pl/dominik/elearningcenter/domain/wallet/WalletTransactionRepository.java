package pl.dominik.elearningcenter.domain.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletTransactionRepository {

    WalletTransaction save(WalletTransaction transaction);

    Page<WalletTransaction> findByUserId(Long userId, Pageable pageable);
}
