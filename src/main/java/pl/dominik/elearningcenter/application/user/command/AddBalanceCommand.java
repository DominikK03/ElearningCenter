package pl.dominik.elearningcenter.application.user.command;

import java.math.BigDecimal;

public record AddBalanceCommand(Long userId, BigDecimal amount) {
}
