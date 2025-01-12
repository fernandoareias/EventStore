package com.fernando;

import com.fernando.events.BankAccountOpened;
import com.fernando.infrastructure.EventStore;

import java.time.Instant;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        var eventStore = new EventStore();
        eventStore.append(new BankAccountOpened(
                UUID.randomUUID(),
                "8257388405513933",
                UUID.randomUUID(),
                "USD",
                Instant.now(),
                1L
        ));

        System.out.println("Iniciando leitura");
        eventStore.stream(System.out::println);

        System.out.println("Lendo o evento de offset 4");
        eventStore.consume(System.out::println, 4L);

        System.out.println("Hello world!");
    }
}