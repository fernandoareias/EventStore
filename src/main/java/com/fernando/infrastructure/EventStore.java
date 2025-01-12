package com.fernando.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public class EventStore implements AutoCloseable {

    private static final String path = "eventStore.txt";
    private MappedByteBuffer buffer;
    private FileChannel fileChannel;
    private RandomAccessFile file;
    private ObjectMapper objectMapper = new ObjectMapper();

    public EventStore() {
        ensureFileExists();
        objectMapper.registerModule(new JavaTimeModule());
        mapFileToMemory();
    }

    private void mapFileToMemory() {
        try {
            file = new RandomAccessFile(path, "rw");
            fileChannel = file.getChannel();
            long fileLength = file.length();
            buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileLength);
        } catch (IOException e) {
            System.err.println("Erro ao mapear o arquivo para memória: " + e.getMessage());
        }
    }

    public void append(Object event) {
        try {
            String jsonEvent = convertObjectToJson(event);
            byte[] eventBytes = jsonEvent.getBytes(StandardCharsets.UTF_8);

            long currentLength = buffer.limit();
            long newLength = currentLength + eventBytes.length + 1;

            buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, newLength);

            buffer.position((int) currentLength);
            buffer.put(eventBytes);
            buffer.put((byte) '\n');
        } catch (IOException e) {
            System.err.println("Erro ao tentar escrever no arquivo de event store: " + e.getMessage());
        }
    }

    public void consume(Consumer<String> action, Long offset) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = null;
            for (int i = 1; i <= offset; i++) {
                line = reader.readLine();
            }

            if(line == null){
                System.out.println("O arquivo não tem tantas linhas.");
                return;
            }
            action.accept(line);
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

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            System.err.println("Erro ao converter objeto para JSON: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        if (buffer != null) {
            buffer.clear();
        }
        if (fileChannel != null) {
            fileChannel.close();
        }
        if (file != null) {
            file.close();
        }
    }
}
