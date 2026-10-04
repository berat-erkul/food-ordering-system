package mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import dto.create.CreateOrderCommand;
import dto.create.CreateOrderResponse;
import dto.create.OrderAddress;
import entity.Order;
import entity.OrderItem;
import entity.Product;
import entity.Restaurant;
import org.springframework.stereotype.Component;
import valueobject.StreetAddress;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand){
        return Restaurant.builder()
                .id(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItem ->
                                new Product(new ProductId(orderItem.getProductId())))
                                .collect(Collectors.toList())
                )
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                //Here I didn't set a value in TrackingId, OrderStatus and FailureMessages fields
                //They are get their values in the initializeOrder() method of Order entity,
                // which is called in OrderDomainService !!!

                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(List<dto.create.OrderItem> orderItems) {

        return orderItems.stream().map(orderItem ->
                OrderItem.builder()
                        .setProduct(new Product(new ProductId(orderItem.getProductId())))
                        .setPrice(new Money(orderItem.getPrice()))
                        .setQuantity(orderItem.getQuantity())
                        .setSubTotal(new Money(orderItem.getSubTotal()))
                        .build()
        ).collect(Collectors.toList());

    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(
                UUID.randomUUID(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity()
        );
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }
}
