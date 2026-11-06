package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.dto.CreateProductCommand;
import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.entity.Product;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.domain.usecase.CreateProductUseCase;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateProductService implements CreateProductUseCase {
    private final ProductRepository products;

    public CreateProductService(ProductRepository products) {
        this.products = products;
    }

    @Override
    public ProductResult execute(CreateProductCommand command) {
        if (command.businessId() == null) {
            throw new IllegalArgumentException("businessId is required");
        }
        Product product = new Product(new ProductId(UUID.randomUUID()), new BusinessId(command.businessId()),
                command.sku(), command.name(), command.description(), new Money(command.unitPrice()),
                command.currency(), command.availableQuantity(), ProductStatus.ACTIVE);
        return ProductResult.from(products.save(product));
    }
}
