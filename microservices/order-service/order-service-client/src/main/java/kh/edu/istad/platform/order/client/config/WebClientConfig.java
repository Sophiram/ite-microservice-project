package kh.edu.istad.platform.order.client.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Provides a {@link WebClient.Builder} that automatically attaches a Bearer JWT
 * (obtained via the client_credentials grant from the Spring Authorization Server)
 * to every outbound request.
 *
 * <p>The registration id "order-service" must match the key under
 * {@code spring.security.oauth2.client.registration} in application.yaml.</p>
 */
@Configuration
public class WebClientConfig {

    /**
     * Reactive authorized-client manager backed by an in-memory client service.
     * Uses client_credentials so no user interaction is needed.
     */
    @Bean
    public ReactiveOAuth2AuthorizedClientManager reactiveAuthorizedClientManager(
            ReactiveClientRegistrationRepository registrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                registrationRepository,
                authorizedClientService
        );
    }

    /**
     * Load-balanced WebClient.Builder with the OAuth2 exchange filter applied.
     * The default client registration is "order-service" (client_credentials).
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        // Default to the "order-service" registration for every request made with this builder
        oauth2Filter.setDefaultClientRegistrationId("order-service");

        return WebClient.builder()
                .filter(oauth2Filter);
    }
}
