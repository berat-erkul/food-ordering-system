package ports.input.message.listener.restaurantapproval;

import dto.message.RestaurantApprovalResponse;

// "Driving Adapter" - call by the "RESTAURANT SERVİCE --> KAFKA"

public interface RestaurantApprovalResponseMessageListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
