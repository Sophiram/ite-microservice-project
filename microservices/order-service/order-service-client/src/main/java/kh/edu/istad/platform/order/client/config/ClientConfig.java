package kh.edu.istad.platform.order.client.config;

import kh.edu.istad.platform.order.client.DownStreamServiceException;
import kh.edu.istad.platform.order.client.httpclient.BusinessHttpClient;
import kh.edu.istad.platform.order.client.httpclient.CustomerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Creates HTTP-interface proxy beans (CustomerHttpClient, BusinessHttpClient) backed by
 * a load-balanced, OAuth2-secured WebClient.
 *
 * <p>Base URLs are read from {@code spring.http.serviceclient.*} in application.yaml so
 * they can be set per-environment without code changes.</p>
 */
@Configuration
public class ClientConfig {

    /**
     * Base URL for customer-service. Default uses Consul/LoadBalancer virtual hostname.
     * Override via {@code spring.http.serviceclient.customer-service.base-url} in application.yaml
     * or the {@code CUSTOMER_SERVICE_BASE_URL} environment variable.
     */
    @Value("${spring.http.serviceclient.customer-service.base-url:lb://customer-service}")
    private String customerServiceBaseUrl;

    /**
     * Base URL for business-service.
     * Override via {@code spring.http.serviceclient.business-service.base-url}.
     */
    @Value("${spring.http.serviceclient.business-service.base-url:lb://business-service}")
    private String businessServiceBaseUrl;

    @Bean
    public CustomerHttpClient customerHttpClient(WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder
                .baseUrl(customerServiceBaseUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        response -> response.createException()
                                .map(ex -> new DownStreamServiceException(
                                        ex.getMessage(), "customer-service"))
                )
                .build();
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build()
                .createClient(CustomerHttpClient.class);
    }

    @Bean
    public BusinessHttpClient businessHttpClient(WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder
                .baseUrl(businessServiceBaseUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        response -> response.createException()
                                .map(ex -> new DownStreamServiceException(
                                        ex.getMessage(), "business-service"))
                )
                .build();
        return HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build()
                .createClient(BusinessHttpClient.class);
    }
}
