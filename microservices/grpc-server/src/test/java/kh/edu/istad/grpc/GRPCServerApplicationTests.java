package kh.edu.istad.grpc;

import com.example.grpc.payment.PaymentResponse;
import kh.edu.istad.grpc.client.PaymentClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GRPCServerApplicationTests {

    @Autowired
    private PaymentClientService paymentClientService;

    @Test
    void testProcessPayment() {
        PaymentResponse response = paymentClientService.executePayment("ORDER-TEST-1", 1000L);
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getPaymentId().startsWith("PAY-"));
    }
}
