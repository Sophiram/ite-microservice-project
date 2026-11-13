package kh.edu.istad.platform.payment.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.dto.PaymentResponse;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.port.out.message.PaymentResponseMessagePublisher;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    public Mono<PaymentResponse> execute(ProcessPaymentCommand command) {
        return Mono.defer(() -> {
            log.info("Processing payment for orderId: {}", command != null ? command.orderId() : null);

            if (command == null) {
                return Mono.error(new PaymentDomainException("ProcessPaymentCommand must not be null"));
            }
            if (command.orderId() == null) {
                return Mono.error(new PaymentDomainException("OrderId must not be null"));
            }
            if (command.customerId() == null) {
                return Mono.error(new PaymentDomainException("CustomerId must not be null"));
            }

            OrderId orderId = new OrderId(command.orderId());
            CustomerId customerId = new CustomerId(command.customerId());

            List<String> failureMessages = new ArrayList<>();
            if (command.price() == null || command.price().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                failureMessages.add("Payment price must be greater than zero");
            }

            Money price = command.price() != null
                    ? new Money(command.price().setScale(2, RoundingMode.HALF_EVEN))
                    : Money.ZERO;

            Payment payment = Payment.builder()
                    .orderId(orderId)
                    .customerId(customerId)
                    .price(price)
                    .build();

            if (!failureMessages.isEmpty()) {
                PaymentFailedEvent failedEvent = paymentDomainService.validateAndFailPayment(payment, failureMessages);
                return paymentRepository.save(payment)
                        .flatMap(saved -> paymentResponseMessagePublisher.publishPaymentFailed(failedEvent)
                                .onErrorResume(e -> {
                                    log.error("Failed to publish PaymentFailedEvent: {}", e.getMessage());
                                    return Mono.empty();
                                })
                                .thenReturn(saved)
                        )
                        .map(saved -> PaymentResponse.from(saved, "Payment processing failed"));
            }

            // Check if payment already exists for this order (Idempotency)
            return paymentRepository.findByOrderId(orderId)
                    .flatMap(existingPayment -> {
                        log.info("Payment already exists for orderId: {}, paymentId: {}",
                                orderId.value(), existingPayment.getId().value());
                        return Mono.just(PaymentResponse.from(existingPayment, "Payment already processed"));
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        PaymentCompletedEvent completedEvent = paymentDomainService.validateAndInitiatePayment(payment, failureMessages);
                        return paymentRepository.save(payment)
                                .flatMap(savedPayment -> paymentResponseMessagePublisher.publishPaymentCompleted(completedEvent)
                                        .onErrorResume(e -> {
                                            log.error("Failed to publish PaymentCompletedEvent for orderId {}: {}",
                                                    orderId.value(), e.getMessage());
                                            return Mono.empty();
                                        })
                                        .thenReturn(savedPayment)
                                )
                                .map(savedPayment -> PaymentResponse.from(savedPayment, "Payment completed successfully"));
                    }));
        });
    }
}
