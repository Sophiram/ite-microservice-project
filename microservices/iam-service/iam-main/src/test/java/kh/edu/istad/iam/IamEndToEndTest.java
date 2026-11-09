package kh.edu.istad.iam;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class IamEndToEndTest {
    private static final String ADMIN_PASSWORD = "TestAdminPassword1!";
    private static final String ADMIN_PASSWORD_HASH = new BCryptPasswordEncoder().encode(ADMIN_PASSWORD);
    private static final String CLIENT_ID = "iam-browser";
    private static final String REDIRECT_URI = "https://oauth.pstmn.io/v1/callback";
    private static final String DCR_SECRET = "test-only-dcr-secret";

    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtDecoder jwtDecoder;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
        registry.add("spring.liquibase.parameters.iam.admin.password-hash", () -> ADMIN_PASSWORD_HASH);
        registry.add("spring.security.oauth2.authorizationserver.issuer", () -> "http://localhost:9000");
        registry.add("iam.authorization-server.dcr-client-secret", () -> DCR_SECRET);
        registry.add("iam.authorization-server.browser-redirect-uri", () -> REDIRECT_URI);
    }

    @Test
    void registrationValidationAndAuthorizationCodeFlowReachProtectedMeEndpoint() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://iam.istad.co/problems/validation-error"))
                .andExpect(jsonPath("$.errors").isArray());

        mvc.perform(get("/login").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Sign in")));
        mvc.perform(get("/login").param("lang", "km").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("<html lang=\"km\">")));

        String verifier = "iam-end-to-end-pkce-verifier-that-is-at-least-forty-three-characters";
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));
        String clientState = "iam-e2e-state";

        URI authorizationRequest = UriComponentsBuilder.fromPath("/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("scope", "openid profile email address phone")
                .queryParam("state", clientState)
                .queryParam("code_challenge", challenge)
                .queryParam("code_challenge_method", "S256")
                .build().encode().toUri();
        MvcResult authorizeStart = mvc.perform(get(authorizationRequest).accept(MediaType.TEXT_HTML))
                .andReturn();
        assertTrue(authorizeStart.getResponse().getStatus() >= 300
                && authorizeStart.getResponse().getStatus() < 400);
        MockHttpSession session = (MockHttpSession) authorizeStart.getRequest().getSession(false);
        assertNotNull(session);

        MvcResult login = mvc.perform(post("/login").session(session).with(csrf())
                        .param("username", "admin")
                        .param("password", ADMIN_PASSWORD))
                .andReturn();
        assertTrue(login.getResponse().getStatus() >= 300 && login.getResponse().getStatus() < 400);
        session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);

        String savedAuthorizeUrl = login.getResponse().getHeader("Location");
        MvcResult authorization = mvc.perform(get(URI.create(savedAuthorizeUrl)).session(session)
                        .accept(MediaType.TEXT_HTML))
                .andReturn();
        String consentLocation = authorization.getResponse().getHeader("Location");
        assertNotNull(consentLocation);
        URI consentUri = URI.create(consentLocation);
        Map<String, String> consentParameters = queryParameters(consentUri.getRawQuery());

        mvc.perform(get(consentUri.getPath()).session(session)
                        .param("client_id", consentParameters.get("client_id"))
                        .param("state", consentParameters.get("state"))
                        .param("scope", consentParameters.get("scope")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Application access")));

        MvcResult consent = mvc.perform(post("/oauth2/authorize").session(session).with(csrf())
                        .param("client_id", consentParameters.get("client_id"))
                        .param("state", consentParameters.get("state"))
                        .param("scope", "profile", "email", "address", "phone"))
                .andReturn();
        assertTrue(consent.getResponse().getStatus() >= 300 && consent.getResponse().getStatus() < 400);
        Map<String, String> callbackParameters = queryParameters(
                URI.create(consent.getResponse().getHeader("Location")).getRawQuery());
        String code = callbackParameters.get("code");
        assertNotNull(code);
        assertEquals(clientState, callbackParameters.get("state"));

        MvcResult tokenResult = mvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("client_id", CLIENT_ID)
                        .param("code", code)
                        .param("redirect_uri", REDIRECT_URI)
                        .param("code_verifier", verifier))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode tokenResponse = objectMapper.readTree(tokenResult.getResponse().getContentAsString());
        String accessToken = tokenResponse.required("access_token").asText();
        String idToken = tokenResponse.required("id_token").asText();

        var accessClaims = jwtDecoder.decode(accessToken);
        String userUuid = accessClaims.getSubject();
        assertTrue(accessClaims.getClaimAsStringList("roles").contains("ADMIN"));
        assertTrue(accessClaims.getClaimAsStringList("permissions").contains("user:read"));
        var idClaims = jwtDecoder.decode(idToken);
        assertEquals(userUuid, idClaims.getSubject());
        assertEquals("admin@example.com", idClaims.getClaimAsString("email"));
        assertEquals("System", idClaims.getClaimAsString("given_name"));

        mvc.perform(get("/api/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(userUuid))
                .andExpect(jsonPath("$.username").value("admin"));
        mvc.perform(get("/api/users").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
        mvc.perform(get("/userinfo").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value(userUuid));

        mvc.perform(get("/").session(session)).andExpect(status().isOk());
        mvc.perform(get("/profile/edit").session(session)).andExpect(status().isOk());
        mvc.perform(get("/profile/password").session(session)).andExpect(status().isOk());
        mvc.perform(get("/profile/accounts").session(session)).andExpect(status().isOk());
        mvc.perform(get("/profile/apps").session(session)).andExpect(status().isOk());
        mvc.perform(get("/register")).andExpect(status().isOk());
        mvc.perform(post("/profile/password").session(session).with(csrf())
                        .param("currentPassword", "").param("newPassword", "short").param("confirmPassword", ""))
                .andExpect(status().isOk());

        String dcrAccessToken = requestDcrToken();
        mvc.perform(post("/connect/register")
                        .header("Authorization", "Bearer " + dcrAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"client_name":"IAM E2E client","redirect_uris":["https://example.com/callback"],
                                 "grant_types":["authorization_code"],"response_types":["code"],
                                 "token_endpoint_auth_method":"none","scope":"openid profile email"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.client_id").isNotEmpty());

        mvc.perform(post("/connect/register")
                        .header("Authorization", "Bearer " + requestDcrToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"client_name":"Invalid scope client","redirect_uris":["https://example.com/callback"],
                                 "grant_types":["authorization_code"],"response_types":["code"],
                                 "token_endpoint_auth_method":"none","scope":"user:read"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_scope"));
    }

    private static Map<String, String> queryParameters(String query) {
        return java.util.Arrays.stream(query.split("&"))
                .map(pair -> pair.split("=", 2))
                .collect(java.util.stream.Collectors.toMap(
                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        pair -> pair.length == 1 ? "" : URLDecoder.decode(pair[1], StandardCharsets.UTF_8)));
    }

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private String requestDcrToken() throws Exception {
        MvcResult result = mvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Authorization", basicAuth("iam-dcr", DCR_SECRET))
                        .param("grant_type", "client_credentials")
                        .param("scope", "client.create"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .required("access_token").asText();
    }
}
