package kh.edu.istad.platform.payment.domain.port.out;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository {

    Mono<Payment> save(Payment payment);

    Mono<Payment> findById(PaymentId paymentId);

    Mono<Payment> findByOrderId(OrderId orderId);

    Flux<Payment> findAll();
}
