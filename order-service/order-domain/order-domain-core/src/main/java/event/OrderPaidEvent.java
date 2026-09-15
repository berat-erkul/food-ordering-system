package event;

import com.food.ordering.system.domain.event.DomainEvent;
import entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvents {

    public OrderPaidEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}