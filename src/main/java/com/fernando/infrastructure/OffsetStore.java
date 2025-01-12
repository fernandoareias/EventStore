package com.fernando.infrastructure;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OffsetStore {

    private static final String path = "offsetStore.txt";
    private Long currentOffset = 0L;

    public OffsetStore() {
        ensureFileExists();
        loadOffsetsFromFile();
    }

    private void ensureFileExists() {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Arquivo de offsets criado: " + path);
            } catch (IOException e) {
                System.err.println("Erro ao criar o arquivo de offsets: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void write() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            currentOffset++;
            writer.append(currentOffset.toString());
        } catch (IOException e) {
            currentOffset--;
            System.err.println("Erro ao tentar escrever no arquivo de offsets: " + e.getMessage());
        }
    }

    private void loadOffsetsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    long offset = Long.parseLong(parts[0].trim());
                    currentOffset = Math.max(currentOffset, offset);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de offsets: " + e.getMessage());
        }
    }

    public Long getCurrentOffset() {
        return currentOffset;
    }
}
