package com.skillnez.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {

    ObjectMapper mapper = new ObjectMapper();

    public String dtoToJson(Object dto) throws JsonProcessingException {
        return mapper.writeValueAsString(dto);
    }

    public String listToJson(Object list) throws JsonProcessingException {
        return mapper.writeValueAsString(list); // Преобразование списка в JSON
    }

    public String stringToJson(String string) throws JsonProcessingException {
        return mapper.writeValueAsString(string);
    }
}
