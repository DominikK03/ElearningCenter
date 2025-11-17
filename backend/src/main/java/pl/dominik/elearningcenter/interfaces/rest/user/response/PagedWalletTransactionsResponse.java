package pl.dominik.elearningcenter.interfaces.rest.user.response;

import java.util.List;
import pl.dominik.elearningcenter.application.wallet.dto.PagedWalletTransactionsDTO;

public record PagedWalletTransactionsResponse(
        List<WalletTransactionResponse> transactions,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static PagedWalletTransactionsResponse from(PagedWalletTransactionsDTO dto) {
        List<WalletTransactionResponse> transactions = dto.transactions().stream()
                .map(WalletTransactionResponse::from)
                .toList();
        return new PagedWalletTransactionsResponse(
                transactions,
                dto.currentPage(),
                dto.totalPages(),
                dto.totalElements()
        );
    }
}
