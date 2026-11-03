package kh.edu.istad.grpc.client;

import com.example.grpc.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentClientService paymentClientService;

    public PaymentController(PaymentClientService paymentClientService) {
        this.paymentClientService = paymentClientService;
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(
            @RequestParam String orderId,
            @RequestParam long amountCents) {

        PaymentResponse response = paymentClientService.executePayment(orderId, amountCents);
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "paymentId", response.getPaymentId(),
                "status", response.getStatus(),
                "message", response.getMessage()
        );
        return ResponseEntity.ok(body);
    }
}
