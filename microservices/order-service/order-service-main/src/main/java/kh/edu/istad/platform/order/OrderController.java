package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.domain.dto.InitialOrderCommand;
import kh.edu.istad.platform.order.domain.dto.InitialOrderResult;
import kh.edu.istad.platform.order.domain.usecase.InitialOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final InitialOrderUseCase initialOrderUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InitialOrderResult> initiateOrder(@RequestBody InitialOrderCommand command) {
        log.info("REST request to initiate order: {}", command);
        return initialOrderUseCase.execute(command);
    }
}
