package kh.edu.istad.platform.order.client.adapter;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.common.grpc.product.v1.BatchGetProductsRequest;
import kh.edu.istad.common.grpc.product.v1.ProductServiceGrpc;
import kh.edu.istad.common.grpc.product.v1.ReleaseStockRequest;
import kh.edu.istad.common.grpc.product.v1.ReserveStockRequest;
import kh.edu.istad.platform.order.client.DownStreamServiceException;
import kh.edu.istad.platform.order.domain.dto.ProductInfo;
import kh.edu.istad.platform.order.domain.dto.StockReservationItem;
import kh.edu.istad.platform.order.domain.dto.StockReservationResult;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.client.ProductServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class ProductServiceGrpcClientAdapter implements ProductServiceClient {
    private final ProductServiceGrpc.ProductServiceBlockingStub stub;
    private final Duration deadline;

    public ProductServiceGrpcClientAdapter(
            ProductServiceGrpc.ProductServiceBlockingStub stub,
            @Value("${service.product.grpc.deadline:3s}") Duration deadline) {
        this.stub = stub;
        this.deadline = deadline;
    }

    @Override
    public Mono<List<ProductInfo>> batchGetProducts(List<ProductId> productIds) {
        return retryIdempotent(() -> {
            var request = BatchGetProductsRequest.newBuilder();
            productIds.forEach(id -> request.addProductIds(id.value().toString()));
            return stub.withDeadlineAfter(deadline.toMillis(), TimeUnit.MILLISECONDS)
                    .batchGetProducts(request.build()).getProductsList().stream()
                    .map(ProductServiceGrpcClientAdapter::toDomain)
                    .toList();
        });
    }

    @Override
    public Mono<StockReservationResult> reserveStock(OrderId orderId, BusinessId businessId,
                                                      List<StockReservationItem> items) {
        return retryIdempotent(() -> {
            var request = ReserveStockRequest.newBuilder()
                    .setOrderId(orderId.value().toString())
                    .setBusinessId(businessId.value().toString());
            items.forEach(item -> request.addItems(
                    kh.edu.istad.common.grpc.product.v1.StockItem.newBuilder()
                            .setProductId(item.productId().value().toString())
                            .setQuantity(item.quantity())
                            .build()));
            var response = stub.withDeadlineAfter(deadline.toMillis(), TimeUnit.MILLISECONDS)
                    .reserveStock(request.build());
            return new StockReservationResult(UUID.fromString(response.getReservationId()),
                    response.getReservedProductsList().stream()
                            .map(ProductServiceGrpcClientAdapter::toDomain).toList());
        });
    }

    @Override
    public Mono<Boolean> releaseStock(OrderId orderId, String reason) {
        return retryIdempotent(() -> stub.withDeadlineAfter(deadline.toMillis(), TimeUnit.MILLISECONDS)
                .releaseStock(ReleaseStockRequest.newBuilder()
                        .setOrderId(orderId.value().toString())
                        .setReason(reason == null ? "" : reason)
                        .build())
                .getReleased());
    }

    private <T> Mono<T> retryIdempotent(java.util.concurrent.Callable<T> call) {
        return Mono.fromCallable(call)
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100))
                        .filter(this::isUnavailable)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .onErrorMap(StatusRuntimeException.class, this::mapException);
    }

    private boolean isUnavailable(Throwable error) {
        return error instanceof StatusRuntimeException statusError
                && statusError.getStatus().getCode() == Status.Code.UNAVAILABLE;
    }

    private RuntimeException mapException(StatusRuntimeException exception) {
        String message = "Product Service gRPC call failed: " + exception.getStatus().getDescription();
        return switch (exception.getStatus().getCode()) {
            case NOT_FOUND, FAILED_PRECONDITION -> new OrderDomainException(message);
            case UNAVAILABLE, DEADLINE_EXCEEDED ->
                    new DownStreamServiceException(message, exception, "product-service");
            default -> new DownStreamServiceException(message, exception, "product-service");
        };
    }

    private static ProductInfo toDomain(kh.edu.istad.common.grpc.product.v1.Product product) {
        return new ProductInfo(new ProductId(UUID.fromString(product.getId())),
                new BusinessId(UUID.fromString(product.getBusinessId())), product.getSku(), product.getName(),
                product.getDescription(), new Money(new BigDecimal(product.getUnitPrice().getAmount())),
                product.getUnitPrice().getCurrency(), product.getAvailableQuantity(),
                product.getStatus().name());
    }
}
