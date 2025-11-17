package pl.dominik.elearningcenter.application.wallet.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.application.wallet.dto.PagedWalletTransactionsDTO;
import pl.dominik.elearningcenter.application.wallet.dto.WalletTransactionDTO;
import pl.dominik.elearningcenter.application.wallet.mapper.WalletTransactionMapper;
import pl.dominik.elearningcenter.domain.wallet.WalletTransaction;
import pl.dominik.elearningcenter.domain.wallet.WalletTransactionRepository;

import java.util.List;

@Service
public class GetUserTransactionsQueryHandler {

    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletTransactionMapper walletTransactionMapper;

    public GetUserTransactionsQueryHandler(
            WalletTransactionRepository walletTransactionRepository,
            WalletTransactionMapper walletTransactionMapper
    ) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletTransactionMapper = walletTransactionMapper;
    }

    @Transactional(readOnly = true)
    public PagedWalletTransactionsDTO handle(GetUserTransactionsQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<WalletTransaction> page = walletTransactionRepository.findByUserId(query.userId(), pageable);

        List<WalletTransactionDTO> transactions = page.getContent().stream()
                .map(walletTransactionMapper::toDto)
                .toList();

        return new PagedWalletTransactionsDTO(
                transactions,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
