package kh.edu.istad.platform.product.domain.usecase;

import kh.edu.istad.platform.product.domain.dto.CreateProductCommand;
import kh.edu.istad.platform.product.domain.dto.ProductResult;

public interface CreateProductUseCase {
    ProductResult execute(CreateProductCommand command);
}
