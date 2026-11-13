package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.persistence.entity.OrderAddressEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.mapper.OrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional
    public Mono<Order> save(Order order) {
        log.info("Saving order: id={}", order.getId() != null ? order.getId().value() : null);

        OrderAddressEntity addressEntity = OrderPersistenceMapper.toAddressEntity(order.getDeliveryAddress());
        OrderEntity orderEntity = OrderPersistenceMapper.toOrderEntity(order);
        List<OrderItemEntity> itemEntities = OrderPersistenceMapper.toOrderItemEntities(order);

        return entityTemplate.select(OrderEntity.class)
                .matching(Query.query(Criteria.where("id").is(order.getId().value())))
                .exists()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return entityTemplate.update(orderEntity)
                                .then(Mono.just(order));
                    } else {
                        Mono<OrderAddressEntity> saveAddress = addressEntity != null
                                ? entityTemplate.insert(addressEntity)
                                : Mono.empty();

                        return saveAddress
                                .then(entityTemplate.insert(orderEntity))
                                .thenMany(Flux.fromIterable(itemEntities)
                                        .flatMap(entityTemplate::insert))
                                .then(Mono.just(order));
                    }
                });
    }

    @Override
    public Mono<Order> findById(OrderId orderId) {
        if (orderId == null || orderId.value() == null) {
            return Mono.empty();
        }
        return entityTemplate.select(OrderEntity.class)
                .matching(Query.query(Criteria.where("id").is(orderId.value())))
                .one()
                .flatMap(this::loadOrderDetails);
    }

    @Override
    public Flux<Order> findAll() {
        return entityTemplate.select(OrderEntity.class)
                .all()
                .flatMap(this::loadOrderDetails);
    }

    @Override
    public Flux<Order> query() {
        return findAll();
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(OrderId orderId) {
        if (orderId == null || orderId.value() == null) {
            return Mono.empty();
        }
        return entityTemplate.delete(OrderItemEntity.class)
                .matching(Query.query(Criteria.where("order_id").is(orderId.value())))
                .all()
                .then(entityTemplate.delete(OrderEntity.class)
                        .matching(Query.query(Criteria.where("id").is(orderId.value())))
                        .all())
                .then();
    }

    private Mono<Order> loadOrderDetails(OrderEntity orderEntity) {
        Mono<OrderAddressEntity> addressMono = orderEntity.getOrderAddressId() != null
                ? entityTemplate.select(OrderAddressEntity.class)
                        .matching(Query.query(Criteria.where("id").is(orderEntity.getOrderAddressId())))
                        .one()
                        .defaultIfEmpty(new OrderAddressEntity())
                : Mono.just(new OrderAddressEntity());

        Mono<List<OrderItemEntity>> itemsMono = entityTemplate.select(OrderItemEntity.class)
                .matching(Query.query(Criteria.where("order_id").is(orderEntity.getId())))
                .all()
                .collectList();

        return Mono.zip(addressMono, itemsMono)
                .map(tuple -> OrderPersistenceMapper.toDomain(orderEntity, tuple.getT1(), tuple.getT2()));
    }
}
