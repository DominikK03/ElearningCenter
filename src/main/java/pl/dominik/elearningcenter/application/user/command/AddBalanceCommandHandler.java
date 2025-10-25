package pl.dominik.elearningcenter.application.user.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;

@Service
public class AddBalanceCommandHandler {

    private final UserRepository userRepository;

    public AddBalanceCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void handle(AddBalanceCommand command) {
        User user = userRepository.findByIdOrThrow(command.userId());

        Money amount = Money.pln(command.amount());
        user.addBalance(amount);

        userRepository.save(user);
    }
}
