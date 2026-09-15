package event;

import com.food.ordering.system.domain.event.DomainEvent;
import entity.Order;

import java.time.ZonedDateTime;

public abstract class OrderEvents implements DomainEvent<Order> {
    private final Order order;
    private final ZonedDateTime createdAt;

    public OrderEvents(Order order, ZonedDateTime createdAt) {
        this.order = order;
        this.createdAt = createdAt;
    }

    public Order getOrder() {
        return order;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
