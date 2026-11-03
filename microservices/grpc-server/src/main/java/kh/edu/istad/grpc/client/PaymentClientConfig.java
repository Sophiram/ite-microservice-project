package kh.edu.istad.grpc.client;

import com.example.grpc.payment.PaymentServiceGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

@Configuration
@ImportGrpcClients(
        target = "payment-service",
        types = PaymentServiceGrpc.PaymentServiceBlockingStub.class
)
public class PaymentClientConfig {
}
