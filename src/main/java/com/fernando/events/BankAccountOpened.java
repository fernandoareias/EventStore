package com.fernando.events;

import java.time.Instant;
import java.util.UUID;

public record BankAccountOpened(
        UUID bankAccountId,
        String accountNumber,
        UUID clienteId,
        String currencyISOCode,
        Instant createdAt,
        Long version
) {
}
