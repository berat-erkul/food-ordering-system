package ports.output.repository;

import entity.Order;
import valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order); //Order "Domain Entity" comes from order-domain-core module

    Optional<Order> findByTrackingId(TrackingId trackingId); //Also TrackingId comes form domain-core module
}
