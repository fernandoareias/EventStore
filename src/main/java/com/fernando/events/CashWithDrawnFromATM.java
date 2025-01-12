package com.fernando.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CashWithDrawnFromATM(
        UUID bankAccountId,
        BigDecimal amount,
        UUID atmId,
        Instant recordedAt,
        long version
) {
}
