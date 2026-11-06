package kh.edu.istad.platform.product.domain.usecase;

import kh.edu.istad.platform.product.domain.dto.ProductResult;

import java.util.List;
import java.util.UUID;

public interface QueryProductUseCase {
    ProductResult findById(UUID id);
    List<ProductResult> findAll();
}
