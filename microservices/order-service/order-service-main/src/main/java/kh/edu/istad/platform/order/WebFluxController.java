package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.config.ServicePropConfig;
import kh.edu.istad.platform.order.domain.dto.QueryCustomerResult;
import kh.edu.istad.platform.order.domain.port.out.client.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RefreshScope
@RequiredArgsConstructor
//@RequestMapping("api/orders")
public class WebFluxController {

    private final CustomerServiceClient customerServiceClient;
    private final ServicePropConfig servicePropConfig;

    @Value("${global.version}")
    private String globalVersion;
    @GetMapping("/consul/config")
    public Mono<Map<String, Object>> getConsulConfig(){
        return Mono.just(
                Map.of("message : ", servicePropConfig.getMessage(),
                        "env :" , servicePropConfig.getEnv(),
                        "version: ", servicePropConfig.getVersion(),
                        "baseUrl: ", servicePropConfig.getBaseUrl(),
                        "globalVersion: ", globalVersion
                        )
        );
    }


    @GetMapping("/test-error")
    public Mono<String> testError() {
        return Mono.error(
                new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Customer Service Internal Error"
                )
        );
    }

    @GetMapping("/customers")
    public Flux<QueryCustomerResult> getAllCustomers() {
        return customerServiceClient.getAllCustomers();
    }

//    @GetMapping("/customers/names")
//    public Flux<String> getCustomerNames() {
//        return customerServiceClient.getCustomerNames();
//    }

    @GetMapping("/flux")
    public Flux<String> getFluxData() {
        return Flux.just("A", "B", "C", "D", "E", "F", "G")
                .log()
                .delayElements(Duration.ofSeconds(1));
    }
}
