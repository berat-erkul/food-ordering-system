package entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money price;
    private final Money subTotal;

    //initializer method of the child must be "non access modifier" to be accessible by the parent class
    //Just reachable by "Order" and "Product" classes - in the same package
    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    boolean isPriceValid(){
        return price.isGreaterThan(Money.ZERO) &&
                price.equals(product.getPrice()) &&
                price.multiply(quantity).equals(subTotal);
    }

    // Constructor is private to enforce the use of the "Builder pattern"
    private OrderItem(Builder builder) {
        price = builder.price;
        super.setId(builder.orderItemId);
        product = builder.product;
        quantity = builder.quantity;
        subTotal = builder.subTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    public static class Builder {
        private OrderItemId orderItemId;
        private Product product;
        private int quantity;
        private Money price;
        private Money subTotal;

        public Builder setId(OrderItemId id) {
            this.orderItemId = id;
            return this;
        }

        public Builder setProduct(Product product) {
            this.product = product;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setPrice(Money price) {
            this.price = price;
            return this;
        }

        public Builder setSubTotal(Money subTotal) {
            this.subTotal = subTotal;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
