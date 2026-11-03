package kh.edu.istad.platform.authserver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.platform.authserver.config.DurationJacksonModule;
import org.junit.jupiter.api.Test;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SerializationTest {

    @Test
    void testSerializationOfClaimsWithInstantAndDuration() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = getClass().getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new DurationJacksonModule());

        Instant now = Instant.now();
        Duration duration = Duration.ofMinutes(30);

        Map<String, Object> claims = new HashMap<>();
        claims.put("nbf", now);
        claims.put("exp", now.plusSeconds(3600));
        claims.put("iat", now);
        claims.put("sub", "user123");
        claims.put("ttl", duration);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("metadata.token.claims", Collections.unmodifiableMap(claims));

        String json = objectMapper.writeValueAsString(metadata);
        System.out.println("Serialized JSON: " + json);

        Map<String, Object> deserialized = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        assertNotNull(deserialized);
        Map<?, ?> deserializedClaims = (Map<?, ?>) deserialized.get("metadata.token.claims");
        assertEquals(now, deserializedClaims.get("nbf"));
        assertEquals(duration, deserializedClaims.get("ttl"));
    }
}
