package kh.edu.istad.iam.rest.error;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;
import kh.edu.istad.iam.rest.dto.ErrorItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException exception) {
        List<ErrorItem> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toError).toList();
        return problem(HttpStatus.BAD_REQUEST, "validation-error", "Validation failed",
                "Request fields are invalid", errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ProblemDetail> methodValidation(HandlerMethodValidationException exception) {
        return problem(HttpStatus.BAD_REQUEST, "validation-error", "Validation failed",
                exception.getMessage(), List.of(new ErrorItem("", "Request parameters are invalid")));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetail> constraintViolation(ConstraintViolationException exception) {
        List<ErrorItem> errors = exception.getConstraintViolations().stream()
                .map(violation -> new ErrorItem(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return problem(HttpStatus.BAD_REQUEST, "validation-error", "Validation failed",
                "Request fields are invalid", errors);
    }

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ProblemDetail> domain(DomainException exception) {
        HttpStatus status = status(exception.getCode());
        String type = switch (exception.getCode()) {
            case VALIDATION_ERROR -> "validation-error";
            case ENTITY_NOT_FOUND -> "not-found";
            case DUPLICATE_ENTITY, IDENTITY_ALREADY_LINKED, LAST_SIGN_IN_METHOD,
                    ASSIGNMENT_ALREADY_REVOKED -> "conflict";
            case SYSTEM_ENTITY_PROTECTED -> "system-entity-protected";
            case ENTITY_INACTIVE, ENTITY_DELETED, ACCOUNT_DISABLED, AUTHENTICATION_FAILED -> "request-rejected";
        };
        return problem(status, type, title(status), exception.getMessage(), List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> illegalArgument(IllegalArgumentException exception) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-request", "Invalid request",
                exception.getMessage(), List.of());
    }

    private ErrorItem toError(FieldError error) {
        return new ErrorItem(error.getField(), error.getDefaultMessage() == null
                ? "Invalid value" : error.getDefaultMessage());
    }

    private HttpStatus status(ErrorCode code) {
        return switch (code) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_ENTITY, IDENTITY_ALREADY_LINKED, LAST_SIGN_IN_METHOD,
                    ASSIGNMENT_ALREADY_REVOKED -> HttpStatus.CONFLICT;
            case SYSTEM_ENTITY_PROTECTED, ENTITY_INACTIVE, ENTITY_DELETED -> HttpStatus.FORBIDDEN;
            case ACCOUNT_DISABLED, AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED;
        };
    }

    private String title(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "Resource not found";
            case CONFLICT -> "Request conflicts with current state";
            case FORBIDDEN -> "Operation is not allowed";
            case UNAUTHORIZED -> "Authentication failed";
            default -> "Request failed";
        };
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String slug, String title,
                                                   String detail, List<ErrorItem> errors) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail == null ? title : detail);
        problem.setType(URI.create("https://iam.istad.co/problems/" + slug));
        problem.setTitle(title);
        problem.setProperty("errors", errors);
        return ResponseEntity.status(status).body(problem);
    }
}
