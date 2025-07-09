package com.stchat.server.web.mapper;

import io.javalin.json.JsonMapper;

public interface CustomJacksonMapperImpl extends JsonMapper {
    String toJsonString(Object obj);

    <T> T fromJsonString(String json, Class<T> targetType);
}
