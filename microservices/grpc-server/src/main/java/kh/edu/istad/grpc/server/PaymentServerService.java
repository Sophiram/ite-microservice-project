package kh.edu.istad.grpc.server;

import com.example.grpc.payment.PaymentRequest;
import com.example.grpc.payment.PaymentResponse;
import com.example.grpc.payment.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class PaymentServerService extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Override
    public void processPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        String orderId = request.getOrderId();
        long amountCents = request.getAmountCents();

        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
        boolean isValid = amountCents > 0;
        String status = isValid ? "SUCCESS" : "FAILED";
        String message = isValid
                ? "Payment of " + amountCents + " cents approved for order " + orderId
                : "Invalid payment amount.";

        PaymentResponse response = PaymentResponse.newBuilder()
                .setPaymentId(paymentId)
                .setStatus(status)
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

