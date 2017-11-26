package com.leon.processor.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class CustomInstantDeserializer extends StdDeserializer<Instant> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public CustomInstantDeserializer() {
        this(null);
    }

    private CustomInstantDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Instant.from(FORMATTER.parse(jsonParser.getText()));
    }

}
