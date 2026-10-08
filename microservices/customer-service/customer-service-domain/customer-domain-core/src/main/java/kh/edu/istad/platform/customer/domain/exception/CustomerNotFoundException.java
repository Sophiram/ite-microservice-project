package kh.edu.istad.platform.customer.domain.exception;

public class CustomerNotFoundException extends CustomerDomainException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(Throwable cause) {
        super(cause);
    }
}
