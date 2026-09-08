package entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money money;
    private final Money subTotal;

    // Constructor is private to enforce the use of the "Builder pattern"
    private OrderItem(Builder builder) {
        money = builder.money;
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

    public Money getMoney() {
        return money;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    public static class Builder {
        private OrderItemId orderItemId;
        private Product product;
        private int quantity;
        private Money money;
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

        public Builder setMoney(Money money) {
            this.money = money;
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
