package pl.dominik.elearningcenter.interfaces.rest.user.request;

import java.math.BigDecimal;

public record AdjustBalanceRequest(
        BigDecimal amount,
        String type,
        String reason
) {
}
