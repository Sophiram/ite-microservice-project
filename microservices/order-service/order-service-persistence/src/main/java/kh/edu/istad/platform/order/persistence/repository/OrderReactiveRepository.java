package kh.edu.istad.platform.order.persistence.repository;

import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderReactiveRepository extends R2dbcRepository<OrderEntity, UUID> {
}
