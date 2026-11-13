package kh.edu.istad.platform.payment.domain.usecase;

import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.port.out.message.PaymentResponseMessagePublisher;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUseCaseTest {

    @Mock
    private PaymentDomainService paymentDomainService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

    private UUID orderId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
    }

    @Test
    void testProcessPayment_Success() {
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                orderId,
                customerId,
                new BigDecimal("150.00")
        );

        when(paymentRepository.findByOrderId(any())).thenReturn(Mono.empty());
        when(paymentDomainService.validateAndInitiatePayment(any(), any()))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.initializePayment();
                    payment.completePayment();
                    return new PaymentCompletedEvent(payment, Instant.now());
                });
        when(paymentRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(paymentResponseMessagePublisher.publishPaymentCompleted(any())).thenReturn(Mono.empty());

        StepVerifier.create(processPaymentUseCase.execute(command))
                .assertNext(response -> {
                    assert response.orderId().equals(orderId);
                    assert response.customerId().equals(customerId);
                    assert response.paymentStatus() == PaymentStatus.COMPLETED;
                })
                .verifyComplete();

        verify(paymentRepository, times(1)).save(any());
        verify(paymentResponseMessagePublisher, times(1)).publishPaymentCompleted(any());
    }
}
