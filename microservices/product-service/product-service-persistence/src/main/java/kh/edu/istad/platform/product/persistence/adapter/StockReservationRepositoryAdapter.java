package kh.edu.istad.platform.product.persistence.adapter;

import kh.edu.istad.platform.product.domain.entity.StockReservation;
import kh.edu.istad.platform.product.domain.port.out.StockReservationRepository;
import kh.edu.istad.platform.product.persistence.mapper.StockReservationPersistenceMapper;
import kh.edu.istad.platform.product.persistence.repository.StockReservationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class StockReservationRepositoryAdapter implements StockReservationRepository {
    private final StockReservationJpaRepository repository;

    public StockReservationRepositoryAdapter(StockReservationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<StockReservation> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).map(StockReservationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<StockReservation> findByOrderIdForUpdate(UUID orderId) {
        return repository.findByOrderIdForUpdate(orderId).map(StockReservationPersistenceMapper::toDomain);
    }

    @Override
    public StockReservation save(StockReservation reservation) {
        return StockReservationPersistenceMapper.toDomain(
                repository.save(StockReservationPersistenceMapper.toEntity(reservation)));
    }
}
