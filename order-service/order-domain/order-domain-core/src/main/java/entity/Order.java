package entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;
import valueobject.OrderItemId;
import valueobject.StreetAddress;
import valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress streetAddress;
    private final Money money;
    private final List<OrderItem> items;

    // non-final fields - I'll change them in business logic after creating the order entity
    private TrackingId trackingId;
    private OrderStatus orderStatus; //comes from common module
    private List<String> failure;

    // public because this is an aggregate root so we want to create it from outside of the aggregate root
    // agg. root must be reachable from outside of the aggregate root
    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }


    private void initializeOrderItems() {
        //we don't need to make it unique, we just use it to say "The -second- Item of this Order".
        long itemId = 1;
        for (OrderItem orderItem: items){
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    //Constructor with builder design pattern (first fill then create with new keyword)
    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        streetAddress = builder.streetAddress;
        money = builder.money;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failure = builder.failure;
    }

    //Builder Method
    public static Builder builder(){
        return new Builder();
    }

    //Getters - NOT INCLUDE ANY SETTER METHODS!
    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public Money getMoney() {
        return money;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailure() {
        return failure;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress streetAddress;
        private Money money;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failure;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder streetAddress(StreetAddress val) {
            streetAddress = val;
            return this;
        }

        public Builder money(Money val) {
            money = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failure(List<String> val) {
            failure = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
