package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.wallet.WalletTransaction;
import pl.dominik.elearningcenter.domain.wallet.WalletTransactionRepository;

@Service
public class AddBalanceCommandHandler {

    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public AddBalanceCommandHandler(UserRepository userRepository,
                                    WalletTransactionRepository walletTransactionRepository) {
        this.userRepository = userRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public void handle(AddBalanceCommand command) {
        User user = userRepository.findByIdOrThrow(command.userId());

        Money amount = Money.pln(command.amount());
        user.addBalance(amount);

        userRepository.save(user);
        walletTransactionRepository.save(
                WalletTransaction.credit(
                        user.getId(),
                        amount.getAmount(),
                        amount.getCurrencyCode(),
                        "Balance top-up",
                        null
                )
        );
    }
}
