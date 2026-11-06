package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.exception.ProductNotFoundException;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.domain.usecase.QueryProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QueryProductService implements QueryProductUseCase {
    private final ProductRepository products;

    public QueryProductService(ProductRepository products) {
        this.products = products;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResult findById(UUID id) {
        return products.findById(id).map(ProductResult::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResult> findAll() {
        return products.findAll().stream().map(ProductResult::from).toList();
    }
}
