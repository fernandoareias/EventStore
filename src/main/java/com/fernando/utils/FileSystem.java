package com.fernando.utils;

public class FileSystem {
    public static void EnsureFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            TryCreateNewFile(file);
        }
    }

    public static void TryCreateNewFile(File file) {
        try {
            CreateNEwFile(file);
        } catch (IOException excpetion) {
            System.err.println("Erro ao criar o arquivo de event store: " + excpetion.getMessage()); //Use logging.
            throw new RuntimeException(excpetion);
        }
    }

    private static void CreateNEwFile(File file) {
        file.createNewFile();
        System.out.println("Arquivo de event store criado: " + file.getPath()); //Use logging.
    }
}