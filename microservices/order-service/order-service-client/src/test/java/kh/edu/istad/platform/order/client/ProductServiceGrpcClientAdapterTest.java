package kh.edu.istad.platform.order.client;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.common.grpc.product.v1.BatchGetProductsRequest;
import kh.edu.istad.common.grpc.product.v1.BatchGetProductsResponse;
import kh.edu.istad.common.grpc.product.v1.Product;
import kh.edu.istad.common.grpc.product.v1.ProductServiceGrpc;
import kh.edu.istad.common.grpc.product.v1.ReleaseStockRequest;
import kh.edu.istad.common.grpc.product.v1.ReleaseStockResponse;
import kh.edu.istad.common.grpc.product.v1.ReserveStockRequest;
import kh.edu.istad.common.grpc.product.v1.ReserveStockResponse;
import kh.edu.istad.platform.order.client.adapter.ProductServiceGrpcClientAdapter;
import kh.edu.istad.platform.order.domain.dto.ProductInfo;
import kh.edu.istad.platform.order.domain.dto.StockReservationItem;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductServiceGrpcClientAdapterTest {
    private Server server;
    private ManagedChannel channel;

    @AfterEach
    void stopServer() {
        if (channel != null) channel.shutdownNow();
        if (server != null) server.shutdownNow();
    }

    @Test
    void mapsProductResponseToDomainDto() {
        var expectedId = UUID.randomUUID();
        var expectedBusinessId = UUID.randomUUID();
        var client = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void batchGetProducts(BatchGetProductsRequest request,
                                         StreamObserver<BatchGetProductsResponse> observer) {
                observer.onNext(BatchGetProductsResponse.newBuilder().addProducts(Product.newBuilder()
                        .setId(expectedId.toString()).setBusinessId(expectedBusinessId.toString())
                        .setSku("SKU-1").setName("Product").setDescription("Description")
                        .setUnitPrice(kh.edu.istad.common.grpc.v1.Money.newBuilder()
                                .setAmount("12.50").setCurrency("USD"))
                        .setAvailableQuantity(4)
                        .setStatus(kh.edu.istad.common.grpc.product.v1.ProductStatus.PRODUCT_STATUS_ACTIVE)
                        .build()).build());
                observer.onCompleted();
            }
        });

        List<ProductInfo> products = client.batchGetProducts(List.of()).block(Duration.ofSeconds(2));

        assertEquals(expectedId, products.getFirst().id().value());
        assertEquals("12.50", products.getFirst().unitPrice().amount().toPlainString());
        assertEquals("USD", products.getFirst().currency());
    }

    @Test
    void reservesStockAndMapsReservationResult() {
        var productId = UUID.randomUUID();
        var businessId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var reservationId = UUID.randomUUID();
        var client = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void reserveStock(ReserveStockRequest request, StreamObserver<ReserveStockResponse> observer) {
                assertEquals(orderId.toString(), request.getOrderId());
                assertEquals(businessId.toString(), request.getBusinessId());
                assertEquals(productId.toString(), request.getItems(0).getProductId());
                assertEquals(2, request.getItems(0).getQuantity());
                observer.onNext(ReserveStockResponse.newBuilder()
                        .setReservationId(reservationId.toString()).build());
                observer.onCompleted();
            }
        });

        var result = client.reserveStock(new OrderId(orderId), new BusinessId(businessId),
                List.of(new StockReservationItem(new ProductId(productId), 2)))
                .block(Duration.ofSeconds(2));

        assertEquals(reservationId, result.reservationId());
    }

    @Test
    void releasesStock() {
        var orderId = UUID.randomUUID();
        var client = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void releaseStock(ReleaseStockRequest request, StreamObserver<ReleaseStockResponse> observer) {
                assertEquals(orderId.toString(), request.getOrderId());
                assertEquals("cancelled", request.getReason());
                observer.onNext(ReleaseStockResponse.newBuilder().setReleased(true).build());
                observer.onCompleted();
            }
        });

        assertTrue(client.releaseStock(new OrderId(orderId), "cancelled").block(Duration.ofSeconds(2)));
    }

    @Test
    void mapsNotFoundToOrderDomainException() {
        var client = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void batchGetProducts(BatchGetProductsRequest request,
                                         StreamObserver<BatchGetProductsResponse> observer) {
                observer.onError(Status.NOT_FOUND.asRuntimeException());
            }
        });

        assertThrows(OrderDomainException.class,
                () -> client.batchGetProducts(List.of()).block(Duration.ofSeconds(2)));
    }

    @Test
    void mapsFailedPreconditionToOrderDomainException() {
        var client = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void batchGetProducts(BatchGetProductsRequest request,
                                         StreamObserver<BatchGetProductsResponse> observer) {
                observer.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            }
        });

        assertThrows(OrderDomainException.class,
                () -> client.batchGetProducts(List.of()).block(Duration.ofSeconds(2)));
    }

    @Test
    void mapsUnavailableAndDeadlineExceededToDownstreamException() {
        var unavailableClient = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void batchGetProducts(BatchGetProductsRequest request,
                                         StreamObserver<BatchGetProductsResponse> observer) {
                observer.onError(Status.UNAVAILABLE.asRuntimeException());
            }
        });
        var unavailable = assertThrows(DownStreamServiceException.class,
                () -> unavailableClient.batchGetProducts(List.of()).block(Duration.ofSeconds(3)));
        assertEquals("product-service", unavailable.getService());

        stopServer();
        var deadlineClient = client(new ProductServiceGrpc.ProductServiceImplBase() {
            @Override
            public void batchGetProducts(BatchGetProductsRequest request,
                                         StreamObserver<BatchGetProductsResponse> observer) {
                observer.onError(Status.DEADLINE_EXCEEDED.asRuntimeException());
            }
        });
        assertThrows(DownStreamServiceException.class,
                () -> deadlineClient.batchGetProducts(List.of()).block(Duration.ofSeconds(2)));
    }

    private ProductServiceGrpcClientAdapter client(ProductServiceGrpc.ProductServiceImplBase service) {
        var name = InProcessServerBuilder.generateName();
        try {
            server = InProcessServerBuilder.forName(name).directExecutor().addService(service).build().start();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not start in-process gRPC server", exception);
        }
        channel = InProcessChannelBuilder.forName(name).directExecutor().build();
        return new ProductServiceGrpcClientAdapter(ProductServiceGrpc.newBlockingStub(channel), Duration.ofSeconds(1));
    }
}
