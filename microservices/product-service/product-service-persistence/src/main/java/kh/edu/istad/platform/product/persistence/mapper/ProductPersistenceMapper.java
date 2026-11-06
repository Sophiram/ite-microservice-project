package kh.edu.istad.platform.product.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.entity.Product;
import kh.edu.istad.platform.product.persistence.entity.ProductEntity;

public final class ProductPersistenceMapper {
    private ProductPersistenceMapper() {
    }

    public static Product toDomain(ProductEntity entity) {
        return new Product(new ProductId(entity.getId()), new BusinessId(entity.getBusinessId()),
                entity.getSku(), entity.getName(), entity.getDescription(), new Money(entity.getUnitPrice()),
                entity.getCurrency(), entity.getAvailableQuantity(), entity.getStatus());
    }

    public static ProductEntity toEntity(Product product) {
        return new ProductEntity(product.id().value(), product.businessId().value(), product.sku(),
                product.name(), product.description(), product.unitPrice().amount(), product.currency(),
                product.availableQuantity(), product.status());
    }
}
