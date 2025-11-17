package pl.dominik.elearningcenter.application.user.command;

import java.math.BigDecimal;

public record AdjustUserBalanceCommand(
        Long userId,
        BigDecimal amount,
        BalanceAdjustmentType type,
        String reason
) {
    public AdjustUserBalanceCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User id cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (type == null) {
            throw new IllegalArgumentException("Adjustment type cannot be null");
        }
    }
}
