package com.stchat.server.web.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.json.JsonMapper;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class CustomJacksonMapper implements JsonMapper {

    private final ObjectMapper objectMapper;

    public CustomJacksonMapper(Consumer<ObjectMapper> configurer) {
        this.objectMapper = new ObjectMapper();
        configurer.accept(this.objectMapper);
    }

    @Override
    public String toJsonString(Object obj, Type type) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    @Override
    public <T> T fromJsonString(String json, Type targetType) {
        try {
            return objectMapper.readValue(json, objectMapper.constructType(targetType));
        } catch (Exception e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }
}
