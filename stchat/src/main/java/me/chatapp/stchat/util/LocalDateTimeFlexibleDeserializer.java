package me.chatapp.stchat.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeFlexibleDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();

        try {
            // Epoch millis (e.g., "1752498610582")
            if (value.matches("\\d{13}")) {
                long millis = Long.parseLong(value);
                return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }

            // Format: "yyyy-MM-dd HH:mm:ss"
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(value, formatter);
            } catch (Exception ignored) {}

            // Format: "HH:mm"
            if (value.matches("\\d{2}:\\d{2}")) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                return LocalDateTime.now()
                        .with(timeFormatter.parse(value, LocalDateTime::from));
            }

        } catch (Exception e) {
            throw new IOException("Unrecognized datetime format: " + value, e);
        }

        throw new IOException("Unrecognized datetime format: " + value);
    }
}
