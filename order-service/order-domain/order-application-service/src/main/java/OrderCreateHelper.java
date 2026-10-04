import dto.create.CreateOrderCommand;
import entity.Customer;
import entity.Order;
import entity.Restaurant;
import event.OrderCreatedEvent;
import exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;
import mapper.OrderDataMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ports.output.repository.CustomerRepository;
import ports.output.repository.OrderRepository;
import ports.output.repository.RestaurantRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    public OrderCreateHelper(OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             CustomerRepository customerRepository,
                             RestaurantRepository restaurantRepository,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional // --> MUST BE PUBLIC
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand){
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);

        //Here first time I've ever calling Business layer
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order,restaurant);
        //Here is my save "command"
        saveOrder(order);
        log.info("Order is created with id:{}",orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        //Here we decided to need a mapper: CreateOrderCommandToRestaurant
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()){
            log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with restaurant id: " + createOrderCommand.getRestaurantId());
        }
        return optionalRestaurant.get();
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()){
            log.warn("Could not find customer with customer id: {}", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customer);
        }
    }

    // in @Transactional we call saveOrder method to save the order to the database, and if it fails,
    // we throw an exception to rollback the transaction
    private Order saveOrder(Order order){
        Order orderResutl = orderRepository.save(order);
        if(orderResutl == null){
            log.error("Could not save order with tracking id: {}", order.getTrackingId());
            throw new OrderDomainException("Could not save order with tracking id: " + order.getTrackingId());
        }
        log.info("Order is saved with id: {}",orderResutl.getId().getValue());
        return orderResutl;
    }
}
