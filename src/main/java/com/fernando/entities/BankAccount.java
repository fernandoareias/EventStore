package com.fernando.entities;

import com.fernando.entities.enums.BankAccountStatus;
import com.fernando.events.BankAccountClosed;
import com.fernando.events.BankAccountOpened;
import com.fernando.events.CashWithDrawnFromATM;
import com.fernando.events.DepositRecorded;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class BankAccount {
    private UUID id;
    private BankAccountStatus status;
    private BigDecimal balance;
    private long version = 0;

    private static final Map<Class<?>, Function<Object, BankAccount>> eventHandlers = Map.of(
            BankAccountOpened.class, event -> create((BankAccountOpened) event),
            DepositRecorded.class, event -> ((BankAccount) event).apply((DepositRecorded) event),
            CashWithDrawnFromATM.class, event -> ((BankAccount) event).apply((CashWithDrawnFromATM) event),
            BankAccountClosed.class, event -> ((BankAccount) event).apply((BankAccountClosed) event)
    );

    private BankAccount(UUID id, BankAccountStatus status, BigDecimal balance, long version) {
        this.id = id;
        this.status = status;
        this.balance = balance;
        this.version = version;
    }

    private static BankAccount create(BankAccountOpened event) {
        return new BankAccount(
                event.bankAccountId(),
                BankAccountStatus.OPENED,
                BigDecimal.ZERO,
                event.version()
        );
    }

    private BankAccount apply(DepositRecorded event){
        this.balance = this.balance.add(event.amount());
        this.version = event.version();
        return this;
    }

    private BankAccount apply(CashWithDrawnFromATM event){
        this.balance = this.balance.subtract(event.amount());
        this.version = event.version();
        return this;
    }

    private BankAccount apply(BankAccountClosed event) {
        this.status = BankAccountStatus.CLOSED;
        this.version = event.version();
        return this;
    }

    public static BankAccount evolve(BankAccount bankAccount, Object event){
        return eventHandlers.getOrDefault(event.getClass(), e -> bankAccount).apply(event);
    }
}
