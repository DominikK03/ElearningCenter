package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.wallet.WalletTransaction;
import pl.dominik.elearningcenter.domain.wallet.WalletTransactionRepository;

@Service
public class AdjustUserBalanceCommandHandler {

    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public AdjustUserBalanceCommandHandler(
            UserRepository userRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public void handle(AdjustUserBalanceCommand command) {
        User user = userRepository.findByIdOrThrow(command.userId());
        Money amount = Money.pln(command.amount());
        String description = command.reason() != null ? command.reason() : "Admin adjustment";

        if (command.type() == BalanceAdjustmentType.CREDIT) {
            user.addBalance(amount);
            walletTransactionRepository.save(
                    WalletTransaction.credit(
                            user.getId(),
                            amount.getAmount(),
                            amount.getCurrencyCode(),
                            description,
                            null
                    )
            );
        } else {
            if (!user.hasEnoughBalance(amount)) {
                throw new DomainException("User does not have enough balance for this deduction");
            }
            user.deductBalance(amount);
            walletTransactionRepository.save(
                    WalletTransaction.debit(
                            user.getId(),
                            amount.getAmount(),
                            amount.getCurrencyCode(),
                            description,
                            null
                    )
            );
        }

        userRepository.save(user);
    }
}
