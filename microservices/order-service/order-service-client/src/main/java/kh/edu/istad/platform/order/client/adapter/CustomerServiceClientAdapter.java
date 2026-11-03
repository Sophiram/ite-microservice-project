package kh.edu.istad.platform.order.client.adapter;

import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Hexagonal adapter: bridges the domain port {@link CustomerServiceClient} to the
 * infrastructure HTTP client {@link CustomerHttpClient}.
 *
 * <p>No OAuth2 or WebClient types leak into this class — the token is injected
 * transparently by the {@link org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction}
 * configured in {@link kh.edu.istad.platform.order.client.config.WebClientConfig}.</p>
 */
@Component
@RequiredArgsConstructor
public class CustomerServiceClientAdapter implements CustomerServiceClient {

    private final CustomerHttpClient customerHttpClient;

    @Override
    public Flux<QueryCustomerResult> getAllCustomers() {
        return customerHttpClient.queryCustomer();
    }
}
