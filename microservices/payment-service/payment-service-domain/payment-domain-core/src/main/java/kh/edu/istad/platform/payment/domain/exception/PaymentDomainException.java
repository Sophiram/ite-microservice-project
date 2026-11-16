package kh.edu.istad.platform.payment.domain.exception;

import kh.edu.istad.common.domain.exception.DomainException;

public class PaymentDomainException extends DomainException {
    public PaymentDomainException(String message) {
        super(message);
    }

    public PaymentDomainException(Throwable cause) {
        super(cause);
    }
}
