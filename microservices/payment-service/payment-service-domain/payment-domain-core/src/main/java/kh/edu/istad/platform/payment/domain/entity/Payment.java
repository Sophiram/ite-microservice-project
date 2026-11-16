package kh.edu.istad.platform.payment.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Payment extends AggregateRoot<PaymentId> {

    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money price;

    private PaymentStatus paymentStatus;
    private Instant createdAt;
    private List<String> failureMessages;

    public Payment(PaymentId paymentId,
                   OrderId orderId,
                   CustomerId customerId,
                   Money price,
                   PaymentStatus paymentStatus,
                   Instant createdAt,
                   List<String> failureMessages) {
        super.setId(paymentId);
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.failureMessages = failureMessages != null ? new ArrayList<>(failureMessages) : new ArrayList<>();
    }

    public void initializePayment() {
        if (getId() != null) {
            throw new PaymentDomainException("Payment is already initialized!");
        }
        setId(new PaymentId(UUID.randomUUID()));
        this.createdAt = Instant.now();
        validatePayment();
    }

    public void validatePayment() {
        if (orderId == null || orderId.value() == null) {
            throw new PaymentDomainException("OrderId cannot be null!");
        }
        if (customerId == null || customerId.value() == null) {
            throw new PaymentDomainException("CustomerId cannot be null!");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new PaymentDomainException("Payment price must be greater than zero!");
        }
    }

    public void completePayment() {
        if (this.paymentStatus != null && this.paymentStatus != PaymentStatus.COMPLETED) {
            throw new PaymentDomainException("Cannot complete payment from state: " + this.paymentStatus);
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void failPayment(List<String> messages) {
        this.paymentStatus = PaymentStatus.FAILED;
        if (messages != null) {
            this.failureMessages = new ArrayList<>(messages);
        }
    }

    public void cancelPayment(List<String> messages) {
        this.paymentStatus = PaymentStatus.CANCELLED;
        if (messages != null) {
            this.failureMessages = new ArrayList<>(messages);
        }
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getPrice() {
        return price;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<String> getFailureMessages() {
        return Collections.unmodifiableList(failureMessages != null ? failureMessages : Collections.emptyList());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PaymentId paymentId;
        private OrderId orderId;
        private CustomerId customerId;
        private Money price;
        private PaymentStatus paymentStatus;
        private Instant createdAt;
        private List<String> failureMessages = new ArrayList<>();

        public Builder paymentId(PaymentId paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder paymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder failureMessages(List<String> failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public Payment build() {
            return new Payment(paymentId, orderId, customerId, price, paymentStatus, createdAt, failureMessages);
        }
    }
}
