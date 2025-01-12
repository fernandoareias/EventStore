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

import com.fernando.utils.FileSystem;
import com.fernando.utils.FileToJsonConverter;

public class EventStore implements AutoCloseable {

    private static final String path = "eventStore.txt";
    private MappedByteBuffer buffer;
    private FileChannel fileChannel;
    private RandomAccessFile file;
    private ObjectMapper objectMapper = new ObjectMapper();

    public EventStore() {
        FileSystem.EnsureFileExists(path);
        objectMapper.registerModule(new JavaTimeModule());
        tryMapFileToMemory();
    }

    private void tryMapFileToMemory() {
        try {
            mapFileToMemory();
        } catch (IOException e) {
            System.err.println("Erro ao mapear o arquivo para memória: " + e.getMessage()); //Use logging.
        }
    }

    private void mapFileToMemory() {
        file = new RandomAccessFile(path, "rw");
        fileChannel = file.getChannel();
        long fileLength = file.length();
        buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileLength);
    }

    public void tryAppend(Object event) {
        try {
            append(event);
        } catch (IOException e) {
            System.err.println("Erro ao tentar escrever no arquivo de event store: " + e.getMessage()); //Use logging.
        }
    }

    private void append(Object event) {
        String jsonEvent = FileToJsonConverter.TryConvertObjectToJson(event);
        byte[] eventBytes = jsonEvent.getBytes(StandardCharsets.UTF_8);

        long currentLength = buffer.limit();
        long newLength = currentLength + eventBytes.length + 1;

        buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, newLength);

        buffer.position((int) currentLength);
        buffer.put(eventBytes);
        buffer.put((byte) '\n');
    }

    public void tryConsume(Consumer<String> action, Long offset) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            consume(reader, action, offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void consume(BufferedReader reader, Consumer<String> action, Long offset) {
        String line = null;
        for (int i = 1; i <= offset; i++) {
            line = reader.readLine();
        }

        if(line == null){
            System.out.println("O arquivo não tem tantas linhas."); //Use logging.
            return;
        }
        action.accept(line);
    }

    public void tryStream(Consumer<String> action) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            stream(reader, action);
        } catch (IOException e) {
            System.err.println("Erro ao tentar ler o arquivo de event store: " + e.getMessage()); //Use logging.
        }
    }

    private void stream(BufferedReader reader, Consumer<String> action) {
        String line;
        while ((line = reader.readLine()) != null) {
            action.accept(line);
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