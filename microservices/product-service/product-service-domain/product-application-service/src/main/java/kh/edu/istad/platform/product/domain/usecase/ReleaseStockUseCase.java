package kh.edu.istad.platform.product.domain.usecase;

import kh.edu.istad.platform.product.domain.dto.ReleaseStockCommand;

public interface ReleaseStockUseCase {
    boolean execute(ReleaseStockCommand command);
}
