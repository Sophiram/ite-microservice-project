package kh.edu.istad.platform.product.grpc;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.Status;
import kh.edu.istad.common.grpc.product.v1.BatchGetProductsRequest;
import kh.edu.istad.common.grpc.product.v1.GetProductRequest;
import kh.edu.istad.common.grpc.product.v1.ProductServiceGrpc;
import kh.edu.istad.common.grpc.product.v1.ReleaseStockRequest;
import kh.edu.istad.common.grpc.product.v1.ReserveStockRequest;
import kh.edu.istad.common.grpc.product.v1.StockItem;
import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.dto.ReserveStockCommand;
import kh.edu.istad.platform.product.domain.dto.StockReservationResult;
import kh.edu.istad.platform.product.domain.exception.InsufficientStockException;
import kh.edu.istad.platform.product.domain.exception.ProductNotFoundException;
import kh.edu.istad.platform.product.domain.usecase.QueryProductUseCase;
import kh.edu.istad.platform.product.domain.usecase.ReleaseStockUseCase;
import kh.edu.istad.platform.product.domain.usecase.ReserveStockUseCase;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductGrpcServiceTest {
    private Server server;
    private ManagedChannel channel;
    private ProductServiceGrpc.ProductServiceBlockingStub stub;
    private final UUID productId = UUID.randomUUID();
    private final UUID businessId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final AtomicReference<ReserveStockCommand> reserveCommand = new AtomicReference<>();
    private RuntimeException nextException;

    @AfterEach
    void stopServer() {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void getProductReturnsMappedProduct() {
        startServer();

        var product = stub.getProduct(GetProductRequest.newBuilder().setProductId(productId.toString()).build());

        assertEquals(productId.toString(), product.getId());
        assertEquals("12.50", product.getUnitPrice().getAmount());
        assertEquals("USD", product.getUnitPrice().getCurrency());
    }

    @Test
    void batchGetProductsReturnsEveryRequestedProduct() {
        startServer();

        var response = stub.batchGetProducts(BatchGetProductsRequest.newBuilder()
                .addProductIds(productId.toString()).addProductIds(UUID.randomUUID().toString()).build());

        assertEquals(2, response.getProductsCount());
    }

    @Test
    void reserveStockInvokesUseCaseAndReturnsReservation() {
        startServer();

        var response = stub.reserveStock(ReserveStockRequest.newBuilder()
                .setOrderId(orderId.toString())
                .setBusinessId(businessId.toString())
                .addItems(StockItem.newBuilder().setProductId(productId.toString()).setQuantity(2))
                .build());

        assertEquals(orderId, reserveCommand.get().orderId());
        assertEquals(2, reserveCommand.get().items().getFirst().quantity());
        assertEquals(orderId.toString(), response.getReservationId());
        assertEquals(1, response.getReservedProductsCount());
    }

    @Test
    void releaseStockReturnsUseCaseResult() {
        startServer();

        var response = stub.releaseStock(ReleaseStockRequest.newBuilder()
                .setOrderId(orderId.toString()).setReason("order cancelled").build());

        assertTrue(response.getReleased());
    }

    @Test
    void mapsNotFoundToGrpcNotFound() {
        nextException = new ProductNotFoundException(productId);
        startServer();

        var error = assertThrows(io.grpc.StatusRuntimeException.class, () -> stub.getProduct(
                GetProductRequest.newBuilder().setProductId(productId.toString()).build()));

        assertEquals(Status.Code.NOT_FOUND, error.getStatus().getCode());
    }

    @Test
    void mapsStockFailureToFailedPrecondition() {
        nextException = new InsufficientStockException(productId, 1, 2);
        startServer();

        var error = assertThrows(io.grpc.StatusRuntimeException.class, () -> stub.reserveStock(
                ReserveStockRequest.newBuilder().setOrderId(orderId.toString())
                        .setBusinessId(businessId.toString())
                        .addItems(StockItem.newBuilder().setProductId(productId.toString()).setQuantity(2))
                        .build()));

        assertEquals(Status.Code.FAILED_PRECONDITION, error.getStatus().getCode());
    }

    @Test
    void mapsValidationFailureToInvalidArgument() {
        nextException = new IllegalArgumentException("bad request");
        startServer();

        var error = assertThrows(io.grpc.StatusRuntimeException.class, () -> stub.releaseStock(
                ReleaseStockRequest.newBuilder().setOrderId(orderId.toString()).build()));

        assertEquals(Status.Code.INVALID_ARGUMENT, error.getStatus().getCode());
    }

    @Test
    void mapsUnexpectedFailureToInternal() {
        nextException = new IllegalStateException("unexpected");
        startServer();

        var error = assertThrows(io.grpc.StatusRuntimeException.class, () -> stub.batchGetProducts(
                BatchGetProductsRequest.newBuilder().addProductIds(productId.toString()).build()));

        assertEquals(Status.Code.INTERNAL, error.getStatus().getCode());
    }

    private void startServer() {
        var query = new QueryProductUseCase() {
            @Override
            public ProductResult findById(UUID id) {
                if (nextException != null) {
                    throw nextException;
                }
                return product(id);
            }

            @Override
            public List<ProductResult> findAll() {
                return List.of();
            }
        };
        var reserve = (ReserveStockUseCase) command -> {
            reserveCommand.set(command);
            if (nextException != null) {
                throw nextException;
            }
            return new StockReservationResult(orderId, List.of(product(productId)));
        };
        var release = (ReleaseStockUseCase) command -> {
            if (nextException != null) {
                throw nextException;
            }
            return true;
        };
        var service = new ProductGrpcService(query, reserve, release);
        var name = InProcessServerBuilder.generateName();
        try {
            server = InProcessServerBuilder.forName(name).directExecutor()
                    .addService(io.grpc.ServerInterceptors.intercept(service,
                            new ProductGrpcExceptionInterceptor(new ProductGrpcExceptionHandler())))
                    .build().start();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not start in-process gRPC server", exception);
        }
        channel = InProcessChannelBuilder.forName(name).directExecutor().build();
        stub = ProductServiceGrpc.newBlockingStub(channel);
    }

    private ProductResult product(UUID id) {
        return new ProductResult(id, businessId, "SKU-1", "Product", "Description",
                new BigDecimal("12.50"), "USD", 5, ProductStatus.ACTIVE);
    }
}
