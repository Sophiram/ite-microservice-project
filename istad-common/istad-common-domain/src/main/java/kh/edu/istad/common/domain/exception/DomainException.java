package kh.edu.istad.common.domain.exception;

public class DomainException extends RuntimeException{
    public DomainException(Throwable cause) {
        super(cause);
    }

    public DomainException(String message) {
        super(message);
    }
}
