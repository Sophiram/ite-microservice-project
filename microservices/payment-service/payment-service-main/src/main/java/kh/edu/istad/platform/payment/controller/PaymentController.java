package kh.edu.istad.platform.payment.controller;

import kh.edu.istad.platform.payment.domain.dto.PaymentResponse;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.domain.usecase.QueryPaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final QueryPaymentUseCase queryPaymentUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PaymentResponse> processPayment(@RequestBody ProcessPaymentCommand command) {
        log.info("REST request to process payment: {}", command);
        return processPaymentUseCase.execute(command);
    }

    @GetMapping("/{paymentId}")
    public Mono<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) {
        log.info("REST request to get payment by id: {}", paymentId);
        return queryPaymentUseCase.getById(paymentId);
    }

    @GetMapping("/order/{orderId}")
    public Mono<PaymentResponse> getPaymentByOrderId(@PathVariable UUID orderId) {
        log.info("REST request to get payment by order id: {}", orderId);
        return queryPaymentUseCase.getByOrderId(orderId);
    }

    @GetMapping
    public Flux<PaymentResponse> getAllPayments() {
        log.info("REST request to get all payments");
        return queryPaymentUseCase.getAll();
    }
}
