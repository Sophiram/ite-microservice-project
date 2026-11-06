package kh.edu.istad.platform.order.domain.port.out.client;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.dto.ProductInfo;
import kh.edu.istad.platform.order.domain.dto.StockReservationItem;
import kh.edu.istad.platform.order.domain.dto.StockReservationResult;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductServiceClient {
    Mono<List<ProductInfo>> batchGetProducts(List<ProductId> productIds);
    Mono<StockReservationResult> reserveStock(OrderId orderId, BusinessId businessId,
                                               List<StockReservationItem> items);
    Mono<Boolean> releaseStock(OrderId orderId, String reason);
}
