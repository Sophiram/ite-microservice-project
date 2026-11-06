package kh.edu.istad.platform.product.persistence.adapter;

import kh.edu.istad.platform.product.domain.entity.Product;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.persistence.mapper.ProductPersistenceMapper;
import kh.edu.istad.platform.product.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {
    private final ProductJpaRepository repository;

    public ProductRepositoryAdapter(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        return ProductPersistenceMapper.toDomain(repository.save(ProductPersistenceMapper.toEntity(product)));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return repository.findById(id).map(ProductPersistenceMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(ProductPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Product> findAllByIdsForUpdate(List<UUID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return repository.findAllByIdsForUpdate(ids).stream().map(ProductPersistenceMapper::toDomain).toList();
    }
}
