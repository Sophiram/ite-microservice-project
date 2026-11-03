package kh.edu.istad.platform.authserver.config;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Custom Jackson module for Java Time types (Duration, Instant) that safely reads and writes
 * typed representations without reflective field access on private JDK fields,
 * ensuring full compatibility with Java 25 JPMS and Spring Authorization Server default typing.
 */
public class DurationJacksonModule extends SimpleModule {

    public DurationJacksonModule() {
        // --- Duration ---
        addSerializer(Duration.class, new JsonSerializer<>() {
            @Override
            public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeNumberField("seconds", value.getSeconds());
                gen.writeNumberField("nano", value.getNano());
                gen.writeEndObject();
            }

            @Override
            public void serializeWithType(Duration value, JsonGenerator gen, SerializerProvider serializers,
                    TypeSerializer typeSer) throws IOException {
                WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
                gen.writeNumberField("seconds", value.getSeconds());
                gen.writeNumberField("nano", value.getNano());
                typeSer.writeTypeSuffix(gen, typeIdDef);
            }
        });

        addDeserializer(Duration.class, new JsonDeserializer<>() {
            @Override
            public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                if (node == null) {
                    return null;
                }
                if (node.isObject()) {
                    long seconds = node.has("seconds") ? node.get("seconds").asLong() : 0;
                    long nano = node.has("nano") ? node.get("nano").asLong() : 0;
                    return Duration.ofSeconds(seconds, nano);
                } else if (node.isNumber()) {
                    return Duration.ofSeconds(node.asLong());
                } else if (node.isTextual()) {
                    return Duration.parse(node.asText());
                }
                return Duration.ZERO;
            }
        });

        // --- Instant ---
        addSerializer(Instant.class, new JsonSerializer<>() {
            @Override
            public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.toString());
            }

            @Override
            public void serializeWithType(Instant value, JsonGenerator gen, SerializerProvider serializers,
                    TypeSerializer typeSer) throws IOException {
                WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.VALUE_STRING));
                gen.writeString(value.toString());
                typeSer.writeTypeSuffix(gen, typeIdDef);
            }
        });

        addDeserializer(Instant.class, new JsonDeserializer<>() {
            @Override
            public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                    return Instant.ofEpochMilli(p.getLongValue());
                }
                String text = p.getText();
                if (text == null || text.isBlank()) {
                    return null;
                }
                try {
                    return Instant.parse(text);
                } catch (Exception ex) {
                    try {
                        return Instant.ofEpochMilli(Long.parseLong(text));
                    } catch (Exception ignored) {
                        return null;
                    }
                }
            }
        });
    }
}
