package kh.edu.istad.platform.product.domain.usecase;

import kh.edu.istad.platform.product.domain.dto.ReserveStockCommand;
import kh.edu.istad.platform.product.domain.dto.StockReservationResult;

public interface ReserveStockUseCase {
    StockReservationResult execute(ReserveStockCommand command);
}
