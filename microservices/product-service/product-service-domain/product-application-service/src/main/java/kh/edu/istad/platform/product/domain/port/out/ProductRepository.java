package kh.edu.istad.platform.product.domain.port.out;

import kh.edu.istad.platform.product.domain.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findAllByIdsForUpdate(List<UUID> ids);
}
