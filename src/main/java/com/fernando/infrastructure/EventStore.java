package com.fernando.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.util.function.Consumer;

public class EventStore {

    private static final String path = "eventStore.txt";
    private ObjectMapper objectMapper = new ObjectMapper();

    public EventStore() {
        ensureFileExists();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void append(Object event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            String jsonEvent = convertObjectToJson(event);

            writer.append(jsonEvent);
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Erro ao tentar escrever no arquivo de event store: " + e.getMessage());
        }
    }

    public void consume(Consumer<String> action, Long offset) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = null;
            for (int i = 1; i < offset; i++) {
                line = reader.readLine();
            }

            if (line != null) {
                action.accept(line);
            } else {
                System.out.println("O arquivo não tem tantas linhas.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void stream(Consumer<String> action) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                action.accept(line);
            }
        } catch (IOException e) {
            System.err.println("Erro ao tentar ler o arquivo de event store: " + e.getMessage());
        }
    }

    private void ensureFileExists() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Arquivo de event store criado: " + path);
            } catch (IOException e) {
                System.err.println("Erro ao criar o arquivo de event store: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            System.err.println("Erro ao converter objeto para JSON: " + e.getMessage());
            return "";
        }
    }
}
