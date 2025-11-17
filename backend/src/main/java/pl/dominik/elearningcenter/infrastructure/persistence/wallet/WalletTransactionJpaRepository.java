package pl.dominik.elearningcenter.infrastructure.persistence.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.dominik.elearningcenter.domain.wallet.WalletTransaction;

interface WalletTransactionJpaRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
