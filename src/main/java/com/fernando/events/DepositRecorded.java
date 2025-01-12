package com.fernando.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositRecorded(
        UUID bankAccountId,
        BigDecimal amount,
        UUID cashierId,
        Instant recordAt,
        Long version
) {
}
