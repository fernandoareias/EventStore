package com.fernando;

import com.fernando.events.BankAccountOpened;
import com.fernando.infrastructure.EventStore;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        try(var eventStore = new EventStore())
        {
            eventStore.tryAppend(new BankAccountOpened(
                    UUID.randomUUID(),
                    "8257388405513933",
                    UUID.randomUUID(),
                    "USD",
                    Instant.now(),
                    1L
            ));

            System.out.println("Iniciando leitura");
            eventStore.tryStream(System.out::println);

            System.out.println("Lendo o evento de offset 4");
            eventStore.tryConsume(System.out::println, 4L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}