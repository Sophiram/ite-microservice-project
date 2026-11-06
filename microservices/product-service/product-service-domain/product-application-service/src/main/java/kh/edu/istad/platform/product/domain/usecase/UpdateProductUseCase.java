package kh.edu.istad.platform.product.domain.usecase;

import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.dto.UpdateProductCommand;

public interface UpdateProductUseCase {
    ProductResult execute(UpdateProductCommand command);
}
