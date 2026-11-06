package kh.edu.istad.platform.product.domain.exception;

import java.util.UUID;

public class ProductNotAvailableException extends RuntimeException {
    public ProductNotAvailableException(UUID productId) {
        super("Product is not active: " + productId);
    }
}
