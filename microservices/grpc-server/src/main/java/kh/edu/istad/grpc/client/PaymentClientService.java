package kh.edu.istad.grpc.client;

import com.example.grpc.payment.PaymentRequest;
import com.example.grpc.payment.PaymentResponse;
import com.example.grpc.payment.PaymentServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class PaymentClientService {

    private final PaymentServiceGrpc.PaymentServiceBlockingStub paymentStub;

    public PaymentClientService(PaymentServiceGrpc.PaymentServiceBlockingStub paymentStub) {
        this.paymentStub = paymentStub;
    }

    public PaymentResponse executePayment(String orderId, long amountCents) {
        PaymentRequest request = PaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setAmountCents(amountCents)
                .build();

        return paymentStub.processPayment(request);
    }
}
