package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.*;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;


import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final BusinessId businessId;
    private final StreetAddress deliveryAddress;
    private final Money price;  // OrderPrice = Total Price
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;
    private Long version;


    // --- start domain logic

    /**
     * Initialize order
     */
    public void initializeOrder(){
        if (super.getId() != null || orderStatus != null){
            throw new OrderDomainException("Order has already been initialized!");
        }
        super.setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        validateOrder();
    }

    // Pay Order
    public void pay(){
        if (orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order state is not correct for pay operation");
        }
        orderStatus = OrderStatus.PAID;
    }

    // approve order
    public void approve(){
        if (orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order state is not correct for approve operation");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    // Init cancel order = Request to cancel
    public void initCancel(List<String> failureMessages){
        if (orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order state is not correct init cancel");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }
    // Cancel order
    public void cancel(){
        if (!(orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.CANCELLING)){
            throw new OrderDomainException("Order state is not correct for cancel");
        }
        orderStatus = OrderStatus.CANCELLED;
    }
    private void updateFailureMessages(List<String> failureMessages){
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(
                    message -> !message.isBlank()
            ).toList());
        }else {
            this.failureMessages = failureMessages;

        }
    }
    // validate order
    public void validateOrder(){

        // validate order price
        if (price == null || !price.isGreaterThanZero()){
            throw new OrderDomainException ("Price must be greater than zero");
        }

        // validate product price
        Money orderSubtotal = items.stream()
                .map(orderItem -> {
                        validateItemPrice(orderItem);
                        return orderItem.getSubTotal();
                        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderSubtotal)){
            throw new OrderDomainException("Price is not equal to order sub total");
        }
    }

    private void validateItemPrice(OrderItem orderItem){
        if (!orderItem.getPrice().equals(orderItem.getProduct().getPrice())){
            throw new OrderDomainException("Price must be equal to product price");
        }
    }
    // -- end domain logic


    public Order(BusinessId businessId, CustomerId customerId, StreetAddress deliveryAddress, Money price, List<OrderItem> items, List<String> failureMessages, OrderStatus orderStatus, TrackingId trackingId, Long version) {
        this.businessId = businessId;
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.items = items;
        this.failureMessages = failureMessages;
        this.orderStatus = orderStatus;
        this.trackingId = trackingId;
        this.version = version;
    }

    public void setFailureMessages(List<String> failureMessages) {
        this.failureMessages = failureMessages;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setTrackingId(TrackingId trackingId) {
        this.trackingId = trackingId;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public BusinessId getBusinessId() {
        return businessId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Money getPrice() {
        return price;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public Long getVersion() {
        return version;
    }

    private Order(Builder builder) {
        super.setId(builder.id);
        businessId = builder.businessId;
        customerId = builder.customerId;
        deliveryAddress = builder.deliveryAddress;
        failureMessages = builder.failureMessages;
        items = builder.items;
        orderStatus = builder.orderStatus;
        price = builder.price;
        trackingId = builder.trackingId;
        version = builder.version;
    }


    public static final class Builder {
        private OrderId id;
        private BusinessId businessId;
        private CustomerId customerId;
        private StreetAddress deliveryAddress;
        private List<String> failureMessages;
        private List<OrderItem> items;
        private OrderStatus orderStatus;
        private Money price;
        private TrackingId trackingId;
        private Long version;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(OrderId val) {
            id = val;
            return this;
        }

        public Builder businessId(BusinessId val) {
            businessId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder version(Long val) {
            version = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
