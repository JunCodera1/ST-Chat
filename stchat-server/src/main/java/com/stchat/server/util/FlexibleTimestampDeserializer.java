package com.stchat.server.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FlexibleTimestampDeserializer extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();

        try {
            if (value.matches("\\d{2}:\\d{2}")) {
                // Chỉ giờ:phút → dùng ngày hôm nay
                LocalTime time = LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm"));
                LocalDateTime now = LocalDateTime.now().withHour(time.getHour()).withMinute(time.getMinute());
                return Timestamp.valueOf(now);
            }

            // Default: yyyy-MM-dd HH:mm:ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return Timestamp.valueOf(LocalDateTime.parse(value, formatter));
        } catch (Exception e) {
            throw new JsonParseException(p, "Invalid timestamp format: " + value, e);
        }
    }
}

