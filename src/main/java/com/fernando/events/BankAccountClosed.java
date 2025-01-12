package com.fernando.events;

import java.time.Instant;
import java.util.UUID;

public record BankAccountClosed(
        UUID bankAccountId,
        String reason,
        Instant closedAt,
        long version
) {
}
