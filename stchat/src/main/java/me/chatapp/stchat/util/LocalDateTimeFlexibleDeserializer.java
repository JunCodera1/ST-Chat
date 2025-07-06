package me.chatapp.stchat.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeFlexibleDeserializer extends StdDeserializer<LocalDateTime> {

    public static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public LocalDateTimeFlexibleDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();

        try {
            return LocalDateTime.parse(text, FULL_FORMATTER);
        } catch (Exception e) {
            try {
                LocalTime time = LocalTime.parse(text, TIME_ONLY_FORMATTER);
                return LocalDateTime.of(LocalDate.now(), time); // Use current date
            } catch (Exception ex) {
                throw new IOException("Unrecognized datetime format: " + text);
            }
        }
    }
}
