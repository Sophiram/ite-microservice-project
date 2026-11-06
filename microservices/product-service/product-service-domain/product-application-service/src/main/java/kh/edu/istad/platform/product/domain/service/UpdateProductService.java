package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.dto.UpdateProductCommand;
import kh.edu.istad.platform.product.domain.exception.ProductNotFoundException;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.domain.usecase.UpdateProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UpdateProductService implements UpdateProductUseCase {
    private final ProductRepository products;

    public UpdateProductService(ProductRepository products) {
        this.products = products;
    }

    @Override
    @Transactional
    public ProductResult execute(UpdateProductCommand command) {
        var product = products.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));
        product.update(command.sku(), command.name(), command.description(), new Money(command.unitPrice()),
                command.currency(), command.availableQuantity(), Objects.requireNonNull(command.status()));
        return ProductResult.from(products.save(product));
    }
}
