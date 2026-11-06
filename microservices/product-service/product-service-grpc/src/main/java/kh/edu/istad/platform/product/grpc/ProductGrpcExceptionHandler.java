package kh.edu.istad.platform.product.grpc;

import io.grpc.Status;
import io.grpc.StatusException;
import kh.edu.istad.platform.product.domain.exception.InsufficientStockException;
import kh.edu.istad.platform.product.domain.exception.ProductNotAvailableException;
import kh.edu.istad.platform.product.domain.exception.ProductNotFoundException;
import kh.edu.istad.platform.product.domain.exception.StockReservationConflictException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class ProductGrpcExceptionHandler implements GrpcExceptionHandler {
    @Override
    public StatusException handleException(Throwable exception) {
        Status status;
        if (exception instanceof ProductNotFoundException) {
            status = Status.NOT_FOUND;
        } else if (exception instanceof InsufficientStockException
                || exception instanceof ProductNotAvailableException) {
            status = Status.FAILED_PRECONDITION;
        } else if (exception instanceof IllegalArgumentException
                || exception instanceof StockReservationConflictException) {
            status = Status.INVALID_ARGUMENT;
        } else {
            status = Status.INTERNAL;
        }
        return status.withDescription(exception.getMessage()).withCause(exception).asException();
    }
}
