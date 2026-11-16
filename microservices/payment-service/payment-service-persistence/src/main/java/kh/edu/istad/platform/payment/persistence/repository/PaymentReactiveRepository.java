package kh.edu.istad.platform.payment.persistence.repository;

import kh.edu.istad.platform.payment.persistence.entity.PaymentEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PaymentReactiveRepository extends R2dbcRepository<PaymentEntity, UUID> {

    Mono<PaymentEntity> findByOrderId(UUID orderId);
}
