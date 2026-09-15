package event;

import entity.Order;

import java.time.ZonedDateTime;

public class OrderCreatedEvent extends OrderEvents {

    public OrderCreatedEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
