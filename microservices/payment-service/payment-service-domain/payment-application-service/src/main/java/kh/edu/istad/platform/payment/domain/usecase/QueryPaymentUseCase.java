package kh.edu.istad.platform.payment.domain.usecase;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.dto.PaymentResponse;
import kh.edu.istad.platform.payment.domain.exception.PaymentNotFoundException;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryPaymentUseCase {

    private final PaymentRepository paymentRepository;

    public Mono<PaymentResponse> getByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(new OrderId(orderId))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Payment not found for order id: " + orderId)))
                .map(payment -> PaymentResponse.from(payment, "Payment retrieved successfully"));
    }

    public Mono<PaymentResponse> getById(UUID paymentId) {
        return paymentRepository.findById(new PaymentId(paymentId))
                .switchIfEmpty(Mono.error(new PaymentNotFoundException("Payment not found for payment id: " + paymentId)))
                .map(payment -> PaymentResponse.from(payment, "Payment retrieved successfully"));
    }

    public Flux<PaymentResponse> getAll() {
        return paymentRepository.findAll()
                .map(payment -> PaymentResponse.from(payment, "Payment retrieved"));
    }
}
