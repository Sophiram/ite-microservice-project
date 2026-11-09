package kh.edu.istad.platform.authserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenFlowIntegrationTest {

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient client = HttpClient.newHttpClient();

    private static String formBody(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                          URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    @Test
    void testClientCredentialsTokenAndProtectedEndpoint() throws Exception {
        String baseUrl = "http://localhost:" + port;
        String basicAuth = Base64.getEncoder().encodeToString("service-client:service-secret".getBytes(StandardCharsets.UTF_8));

        // 1. Request access token via Client Credentials grant
        String requestBody = formBody(Map.of(
                "grant_type", "client_credentials",
                "scope", "api.read"
        ));

        HttpRequest tokenReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/oauth2/token"))
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> tokenRes = client.send(tokenReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("Token Response: " + tokenRes.body());
        assertEquals(200, tokenRes.statusCode());

        JsonNode jsonNode = objectMapper.readTree(tokenRes.body());
        String accessToken = jsonNode.get("access_token").asText();
        assertNotNull(accessToken);
        assertEquals("Bearer", jsonNode.get("token_type").asText());
        assertTrue(jsonNode.get("expires_in").asLong() > 0);

        // 2. Call protected endpoint /api/hello with Bearer token
        HttpRequest apiReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/hello"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> apiRes = client.send(apiReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("API /api/hello Response: " + apiRes.body());
        assertEquals(200, apiRes.statusCode());
    }

    @Test
    void testAuthorizationCodeAndRefreshTokenFlow() throws Exception {
        String baseUrl = "http://localhost:" + port;

        // PKCE setup
        String codeVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);

        java.net.CookieManager cookieManager = new java.net.CookieManager();
        HttpClient authClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        // 1. Initial authorize request (redirects to login)
        String authorizeUrl = baseUrl + "/oauth2/authorize?" + formBody(Map.of(
                "response_type", "code",
                "client_id", "postman-client",
                "scope", "openid profile api.read",
                "redirect_uri", "https://oauth.pstmn.io/v1/callback",
                "code_challenge", codeChallenge,
                "code_challenge_method", "S256"
        ));

        HttpRequest authReq = HttpRequest.newBuilder().uri(URI.create(authorizeUrl)).GET().build();
        HttpResponse<String> authRes = authClient.send(authReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, authRes.statusCode());
        String loginPageUrl = authRes.headers().firstValue("Location").orElse(baseUrl + "/login");

        // Fetch login page to get CSRF token
        HttpRequest loginPageReq = HttpRequest.newBuilder().uri(URI.create(loginPageUrl)).GET().build();
        HttpResponse<String> loginPageRes = authClient.send(loginPageReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginPageRes.statusCode());
        String loginPageHtml = loginPageRes.body();
        System.out.println("Login page HTML:\n" + loginPageHtml);

        // Extract CSRF token
        java.util.regex.Matcher csrfMatcher = java.util.regex.Pattern.compile("name=\"_csrf\"[^>]*?value=\"([^\"]+)\"").matcher(loginPageHtml);
        assertTrue(csrfMatcher.find(), "CSRF token not found in login page");
        String csrfToken = csrfMatcher.group(1);

        // 2. Perform form login with CSRF token
        String loginBody = formBody(Map.of(
                "username", "user",
                "password", "password",
                "_csrf", csrfToken
        ));
        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();
        HttpResponse<String> loginRes = authClient.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, loginRes.statusCode());

        // 3. Retry authorize request with authenticated session
        HttpResponse<String> codeRedirectRes = authClient.send(authReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, codeRedirectRes.statusCode());
        String location = codeRedirectRes.headers().firstValue("Location").orElseThrow();
        assertTrue(location.startsWith("https://oauth.pstmn.io/v1/callback?code="));

        // Extract authorization code
        String code = URI.create(location).getQuery().split("&")[0].split("=")[1];
        assertNotNull(code);

        // 4. Exchange code for access_token and refresh_token
        String postmanBasicAuth = Base64.getEncoder().encodeToString("postman-client:postman-secret".getBytes(StandardCharsets.UTF_8));
        String tokenBody = formBody(Map.of(
                "grant_type", "authorization_code",
                "code", code,
                "redirect_uri", "https://oauth.pstmn.io/v1/callback",
                "code_verifier", codeVerifier
        ));

        HttpRequest exchangeReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/oauth2/token"))
                .header("Authorization", "Basic " + postmanBasicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(tokenBody))
                .build();

        HttpResponse<String> exchangeRes = client.send(exchangeReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("Authorization Code Token Response: " + exchangeRes.body());
        assertEquals(200, exchangeRes.statusCode());

        JsonNode tokensJson = objectMapper.readTree(exchangeRes.body());
        String initialAccessToken = tokensJson.get("access_token").asText();
        String refreshToken = tokensJson.get("refresh_token").asText();
        assertNotNull(initialAccessToken);
        assertNotNull(refreshToken);

        // 5. Use refresh_token to obtain a new access_token (Refresh Token grant)
        String refreshBody = formBody(Map.of(
                "grant_type", "refresh_token",
                "refresh_token", refreshToken
        ));

        HttpRequest refreshReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/oauth2/token"))
                .header("Authorization", "Basic " + postmanBasicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(refreshBody))
                .build();

        HttpResponse<String> refreshRes = client.send(refreshReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("Refresh Token Response: " + refreshRes.body());
        assertEquals(200, refreshRes.statusCode());

        JsonNode refreshedTokens = objectMapper.readTree(refreshRes.body());
        String newAccessToken = refreshedTokens.get("access_token").asText();
        String newRefreshToken = refreshedTokens.get("refresh_token").asText();
        assertNotNull(newAccessToken);
        assertNotNull(newRefreshToken);

        // 6. Test protected endpoint /api/hello with the refreshed access token
        HttpRequest apiReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/hello"))
                .header("Authorization", "Bearer " + newAccessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> apiRes = client.send(apiReq, HttpResponse.BodyHandlers.ofString());
        System.out.println("Refreshed API /api/hello Response: " + apiRes.body());
        assertEquals(200, apiRes.statusCode());
    }
}
