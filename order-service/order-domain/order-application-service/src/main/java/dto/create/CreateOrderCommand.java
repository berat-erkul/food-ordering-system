package dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {

    // Mapper will map the fields of this class to the fields of the Order entity, which is defined in the domain layer.
    // The fields of this class are not defined by the ValueObjects in the domain layer, but they are used to create an Order entity.

    @NotNull
    private final UUID customerId; //Not defined by "CustomerID" ValueObject
    @NotNull
    private final UUID restaurantId; //Not defined by "RestaurantID" ValueObject
    @NotNull
    private final BigDecimal price; //Not defined by "Money" ValueObject
    @NotNull
    private final List<OrderItem> items;
    @NotNull
    private final OrderAddress address;

}
