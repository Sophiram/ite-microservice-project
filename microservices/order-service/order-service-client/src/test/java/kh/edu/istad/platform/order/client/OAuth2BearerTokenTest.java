package kh.edu.istad.platform.order.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that proves:
 * 1. The "Authorization: Bearer <token>" header is attached to downstream calls.
 * 2. The token is reused (only one token-endpoint call for two customer-service calls).
 *
 * Uses MockWebServer to simulate both the auth-server token endpoint and customer-service.
 */
class OAuth2BearerTokenTest {

    private static final String STUB_TOKEN = "stub-access-token-12345";
    private static final String CLIENT_ID = "order-service";
    private static final String CLIENT_SECRET = "order-secret";

    private MockWebServer authServerMock;
    private MockWebServer customerServiceMock;
    private CustomerHttpClient customerHttpClient;

    @BeforeEach
    void setUp() throws IOException {
        authServerMock = new MockWebServer();
        authServerMock.start();
        customerServiceMock = new MockWebServer();
        customerServiceMock.start();

        String tokenUri = authServerMock.url("/oauth2/token").toString();
        String baseUrl  = customerServiceMock.url("/").toString();

        // Build a minimal client registration pointing at the mock auth server
        ClientRegistration registration = ClientRegistration
                .withRegistrationId(CLIENT_ID)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(tokenUri)
                .scope("customer.read")
                .build();

        ReactiveClientRegistrationRepository registrationRepository =
                new InMemoryReactiveClientRegistrationRepository(registration);

        ReactiveOAuth2AuthorizedClientService authorizedClientService =
                new InMemoryReactiveOAuth2AuthorizedClientService(registrationRepository);

        // Use a real WebClient-backed token response client so the filter fetches the token
        WebClientReactiveClientCredentialsTokenResponseClient tokenClient =
                new WebClientReactiveClientCredentialsTokenResponseClient();

        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        registrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(
                org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials(cc -> cc.accessTokenResponseClient(tokenClient))
                        .build()
        );

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth2Filter.setDefaultClientRegistrationId(CLIENT_ID);

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(oauth2Filter)
                .build();

        customerHttpClient = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build()
                .createClient(CustomerHttpClient.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        authServerMock.shutdown();
        customerServiceMock.shutdown();
    }

    /**
     * Enqueues a token response on the auth server mock, then two customer-service responses.
     * Asserts that:
     * - The Authorization header is "Bearer stub-access-token-12345" on both calls.
     * - The token endpoint was called only ONCE (token reuse).
     */
    @Test
    void bearerTokenIsAttachedAndReused() throws InterruptedException {
        // --- Auth-server mock: return a valid token ---
        String tokenJson = """
                {
                  "access_token": "%s",
                  "token_type": "Bearer",
                  "expires_in": 1800,
                  "scope": "customer.read"
                }
                """.formatted(STUB_TOKEN);

        authServerMock.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(tokenJson));

        // --- Customer-service mock: two responses ---
        String customersJson = """
                [{"username":"alice","email":"alice@example.com"},
                 {"username":"bob","email":"bob@example.com"}]
                """;
        customerServiceMock.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(customersJson));
        customerServiceMock.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(customersJson));

        // --- First call ---
        Flux<QueryCustomerResult> first = customerHttpClient.queryCustomer();
        StepVerifier.create(first)
                .expectNextMatches(r -> "alice".equals(r.username()))
                .expectNextMatches(r -> "bob".equals(r.username()))
                .verifyComplete();

        // --- Verify auth header on first customer-service request ---
        RecordedRequest customerReq1 = customerServiceMock.takeRequest();
        assertThat(customerReq1.getHeader(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer " + STUB_TOKEN);

        // --- Second call (should reuse the cached token) ---
        Flux<QueryCustomerResult> second = customerHttpClient.queryCustomer();
        StepVerifier.create(second)
                .expectNextMatches(r -> "alice".equals(r.username()))
                .expectNextMatches(r -> "bob".equals(r.username()))
                .verifyComplete();

        RecordedRequest customerReq2 = customerServiceMock.takeRequest();
        assertThat(customerReq2.getHeader(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer " + STUB_TOKEN);

        // --- Token endpoint should have been called ONLY ONCE (token reuse) ---
        assertThat(authServerMock.getRequestCount())
                .as("Token endpoint should be called exactly once; token must be reused")
                .isEqualTo(1);
    }
}
