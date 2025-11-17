package pl.dominik.elearningcenter.application.wallet.dto;

import java.util.List;

public record PagedWalletTransactionsDTO(
        List<WalletTransactionDTO> transactions,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
