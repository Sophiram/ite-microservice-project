package kh.edu.istad.platform.payment.domain.port.out.message;

import kh.edu.istad.platform.payment.domain.event.PaymentCancelledEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import reactor.core.publisher.Mono;

public interface PaymentResponseMessagePublisher {

    Mono<Void> publishPaymentCompleted(PaymentCompletedEvent event);

    Mono<Void> publishPaymentFailed(PaymentFailedEvent event);

    Mono<Void> publishPaymentCancelled(PaymentCancelledEvent event);
}
