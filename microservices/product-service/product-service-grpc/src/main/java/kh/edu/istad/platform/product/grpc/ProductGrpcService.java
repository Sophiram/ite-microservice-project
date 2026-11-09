package kh.edu.istad.platform.product.grpc;

import build.buf.gen.istad.product.v1.BatchGetProductsRequest;
import build.buf.gen.istad.product.v1.BatchGetProductsResponse;
import build.buf.gen.istad.product.v1.GetProductRequest;
import build.buf.gen.istad.product.v1.GetProductResponse;
import build.buf.gen.istad.product.v1.ProductServiceGrpc;
import build.buf.gen.istad.product.v1.ReleaseStockRequest;
import build.buf.gen.istad.product.v1.ReleaseStockResponse;
import build.buf.gen.istad.product.v1.ReserveStockRequest;
import build.buf.gen.istad.product.v1.ReserveStockResponse;
import kh.edu.istad.platform.product.domain.dto.ReleaseStockCommand;
import kh.edu.istad.platform.product.domain.dto.ReserveStockCommand;
import kh.edu.istad.platform.product.domain.usecase.QueryProductUseCase;
import kh.edu.istad.platform.product.domain.usecase.ReleaseStockUseCase;
import kh.edu.istad.platform.product.domain.usecase.ReserveStockUseCase;
import kh.edu.istad.platform.product.grpc.mapper.ProductGrpcMapper;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private final QueryProductUseCase queryProducts;
    private final ReserveStockUseCase reserveStock;
    private final ReleaseStockUseCase releaseStock;

    public ProductGrpcService(QueryProductUseCase queryProducts, ReserveStockUseCase reserveStock,
                              ReleaseStockUseCase releaseStock) {
        this.queryProducts = queryProducts;
        this.reserveStock = reserveStock;
        this.releaseStock = releaseStock;
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<GetProductResponse> observer) {
        observer.onNext(ProductGrpcMapper.toGetProductResponse(
                queryProducts.findById(UUID.fromString(request.getProductId()))));
        observer.onCompleted();
    }

    @Override
    public void batchGetProducts(BatchGetProductsRequest request,
                                 StreamObserver<BatchGetProductsResponse> observer) {
        var response = BatchGetProductsResponse.newBuilder();
        request.getProductIdsList().forEach(id -> response.addProducts(
                ProductGrpcMapper.toProto(queryProducts.findById(UUID.fromString(id)))));
        observer.onNext(response.build());
        observer.onCompleted();
    }

    @Override
    public void reserveStock(ReserveStockRequest request, StreamObserver<ReserveStockResponse> observer) {
        var command = new ReserveStockCommand(UUID.fromString(request.getOrderId()),
                UUID.fromString(request.getBusinessId()),
                request.getItemsList().stream()
                        .map(item -> new ReserveStockCommand.Item(
                                UUID.fromString(item.getProductId()), item.getQuantity()))
                        .toList());
        var result = reserveStock.execute(command);
        var response = ReserveStockResponse.newBuilder().setReservationId(result.reservationId().toString());
        result.products().forEach(product -> response.addReservedProducts(ProductGrpcMapper.toProto(product)));
        observer.onNext(response.build());
        observer.onCompleted();
    }

    @Override
    public void releaseStock(ReleaseStockRequest request, StreamObserver<ReleaseStockResponse> observer) {
        boolean released = releaseStock.execute(new ReleaseStockCommand(
                UUID.fromString(request.getOrderId()), request.getReason()));
        observer.onNext(ReleaseStockResponse.newBuilder().setReleased(released).build());
        observer.onCompleted();
    }
}
