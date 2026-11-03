package kh.edu.istad.grpc.client;

import com.example.grpc.payment.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientRunner.class);
    private final PaymentClientService paymentClientService;

    public PaymentClientRunner(PaymentClientService paymentClientService) {
        this.paymentClientService = paymentClientService;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Testing gRPC client call to PaymentService...");
            PaymentResponse response = paymentClientService.executePayment("ORDER-12345", 5000L);
            log.info("Received gRPC Response: paymentId={}, status={}, message={}",
                    response.getPaymentId(), response.getStatus(), response.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute gRPC test payment: {}", e.getMessage());
        }
    }
}
