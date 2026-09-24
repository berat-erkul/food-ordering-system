package ports.input.service;

import dto.create.CreateOrderCommand;
import dto.create.CreateOrderResponse;
import dto.track.TrackOrderQuery;
import dto.track.TrackOrderResponse;

import javax.validation.Valid;

// "Driving Adapter" - call by the "REST" Controller

public interface OrderApplicationService {

    // Here I defined a little service contract for the REST controller to use.
    // The REST controller will call this service to create an order and track an order.

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);

}
