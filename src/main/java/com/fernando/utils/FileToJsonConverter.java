package com.fernando.utils;

public class FileToJsonConverter {
    public static String TryConvertObjectToJson(Object object) throws JsonProcessingException {
        try {
            return convertObjectToJson();
        } catch (IOException exception) {
            System.err.println("Erro ao converter objeto para JSON: " + exception.getMessage()); //Use logging.
            throw exception;
        }
    }

    private static String convertObjectToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}