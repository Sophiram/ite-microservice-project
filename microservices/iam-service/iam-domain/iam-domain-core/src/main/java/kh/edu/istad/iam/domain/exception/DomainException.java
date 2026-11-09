package kh.edu.istad.iam.domain.exception;

public class DomainException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private final ErrorCode code;

    public DomainException(ErrorCode code, String message){
        super(message);
        this.code = code;
    }

    public ErrorCode getCode(){
        return code;
    }
}
