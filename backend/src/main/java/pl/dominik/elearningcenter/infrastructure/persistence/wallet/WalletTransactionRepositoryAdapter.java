package pl.dominik.elearningcenter.infrastructure.persistence.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.dominik.elearningcenter.domain.wallet.WalletTransaction;
import pl.dominik.elearningcenter.domain.wallet.WalletTransactionRepository;

@Repository
public class WalletTransactionRepositoryAdapter implements WalletTransactionRepository {

    private final WalletTransactionJpaRepository jpaRepository;

    public WalletTransactionRepositoryAdapter(WalletTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WalletTransaction save(WalletTransaction transaction) {
        return jpaRepository.save(transaction);
    }

    @Override
    public Page<WalletTransaction> findByUserId(Long userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
