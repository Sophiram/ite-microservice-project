package kh.edu.istad.platform.product.grpc.mapper;

import kh.edu.istad.common.grpc.product.v1.GetProductResponse;
import kh.edu.istad.common.grpc.product.v1.Product;
import kh.edu.istad.common.grpc.product.v1.ProductStatus;
import kh.edu.istad.common.grpc.v1.Money;
import kh.edu.istad.platform.product.domain.dto.ProductResult;

public final class ProductGrpcMapper {
    private ProductGrpcMapper() {
    }

    public static Product toProto(ProductResult product) {
        return Product.newBuilder()
                .setId(product.id().toString())
                .setBusinessId(product.businessId().toString())
                .setSku(product.sku())
                .setName(product.name())
                .setDescription(product.description())
                .setUnitPrice(Money.newBuilder()
                        .setAmount(product.unitPrice().toPlainString())
                        .setCurrency(product.currency())
                        .build())
                .setAvailableQuantity(product.availableQuantity())
                .setStatus(toProto(product.status()))
                .build();
    }

    public static GetProductResponse toGetProductResponse(ProductResult productResult) {
        Product product = toProto(productResult);
        return GetProductResponse.newBuilder()
                .setId(product.getId())
                .setBusinessId(product.getBusinessId())
                .setSku(product.getSku())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setUnitPrice(product.getUnitPrice())
                .setAvailableQuantity(product.getAvailableQuantity())
                .setStatus(product.getStatus())
                .build();
    }

    private static ProductStatus toProto(kh.edu.istad.platform.product.domain.valueobject.ProductStatus status) {
        return switch (status) {
            case ACTIVE -> ProductStatus.PRODUCT_STATUS_ACTIVE;
            case INACTIVE -> ProductStatus.PRODUCT_STATUS_INACTIVE;
            case DISCONTINUED -> ProductStatus.PRODUCT_STATUS_DISCONTINUED;
        };
    }
}
