import entity.Order;
import entity.Restaurant;
import event.OrderCancelledEvent;
import event.OrderCreatedEvent;
import event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    //finish line
    void approveOrder (Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    //finish line
    void cancelOrder(Order order, List<String> failureMessages);


}
