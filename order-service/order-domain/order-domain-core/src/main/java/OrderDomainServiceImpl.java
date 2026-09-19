import com.food.ordering.system.domain.valueobject.ProductId;
import entity.Order;
import entity.Product;
import entity.Restaurant;
import event.OrderCancelledEvent;
import event.OrderCreatedEvent;
import event.OrderPaidEvent;
import exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
        // 1) Restaurant.isActive() — Restaurant aggregate'inin kendi kuralı, biz sadece çağırıyoruz
        validateRestaurant(restaurant);

        // 2) Client'ın gönderdiği order item'lardaki Product bilgisini (name/price)
        //    restoranın DB'den gelen GERÇEK verisiyle üzerine yazıyoruz.
        //    Bu adım olmadan aşağıdaki validateOrder() client'ın kendi uydurduğu
        //    fiyatı yine client'ın uydurduğu fiyatla karşılaştırır — anlamsızlaşır.
        setOrderProductInformation(order, restaurant);

        // 3) Order.validateOrder() → Order'ın KENDİ invariant zinciri çalışır:
        //      validateInitialOrder()  — daha önce initialize edilmemiş mi
        //      validateTotalPrice()    — price > 0 mu
        //      validateItemsPrice()    — her item için validateItemPrice(orderItem)
        //                                 → orderItem.isPriceValid() burada (2)'de
        //                                   bastığımız GERÇEK restoran fiyatına bakıyor
        //    Herhangi biri throw ederse akış burada durur, initializeOrder()'a hiç gelinmez (fail-fast).
        order.validateOrder();

        // 4) Sadece (3) tamamen temiz geçtiyse: id + trackingId + PENDING + item id'leri atanır.
        order.initializeOrder();

        log.info("Order with id: {} is initiated", order.getId().getValue());

        // 5) Domain service SADECE event'i üretip DÖNDÜRÜR, yayınlamaz (fire etmez).
        //    Yayınlama kararı (Kafka'ya basma) order-application-service'in işi —
        //    çünkü event ancak DB'ye persist işlemi başarılı olduktan sonra fırlatılmalı.
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));
    }


    @Override
    public OrderPaidEvent payOrder(Order order) {
        // TODO: order.pay() çağrılacak (PENDING → PAID), sonra OrderPaidEvent üretilip dönecek.
        return null;
    }

    @Override
    public void approveOrder(Order order) {
        // TODO: order.approve() çağrılacak (PAID → APPROVED). Event yok — saga'nın son adımı.
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        // TODO: order.initCancel(failureMessages) çağrılacak (PAID → CANCELLING),
        //       sonra OrderCancelledEvent üretilip dönecek (Payment servisine iade sinyali).
        return null;
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        // TODO: order.cancel(failureMessages) çağrılacak (PENDING|CANCELLING → CANCELLED). Event yok.
    }

    // Restaurant aggregate'inin kuralını burada TEKRAR YAZMIYORUZ, sadece tetikliyoruz.
    // Kural (aktiflik kontrolü) Restaurant'ın kendi sorumluluğunda olmalı — şu an
    // Restaurant.isActive() sadece bir getter, kuralı burada if ile kontrol ediyoruz.
    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() + " is currently not active");
        }
    }

    // Her order item'ı, id'si eşleşen restaurant product'ı ile eşleştirip
    // Product'ın name/price alanlarını restoranın GERÇEK değerleriyle günceller.
    // Bu satırdan sonra orderItem.getProduct().getPrice() artık client'ın dediği değil,
    // restoranın DB'deki gerçek fiyatıdır — validateItemPrice() bunu baz alacak.
    //
    // ESKİ HALİ — iç içe forEach, O(n × m):
    // n = order.getItems() boyutu, m = restaurant.getProducts() boyutu.
    // Her orderItem için restaurant.getProducts() BAŞTAN SONA taranıyordu.
    // Ayrıca eşleşme bulunamazsa (client restoranda olmayan bir productId gönderirse)
    // sessizce hiçbir şey yapmıyordu — bu bir güvenlik açığıydı, aşağıdaki yeni
    // sürümde `throw` ile kapatıldı.
    //
    // private void setOrderProductInformation(Order order, Restaurant restaurant) {
    //     order.getItems().forEach(orderItem -> restaurant.getProducts().forEach(restaurantProduct -> {
    //
    //         if (orderItem.getProduct().getId().equals(restaurantProduct.getId())) {
    //             orderItem.getProduct().updateWithConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
    //         }
    //
    //     }));
    // }

    // YENİ HALİ — restaurant.getProducts() ÖNCE id'ye göre Map'e çevrilir (tek geçiş, O(m)),
    // sonra her orderItem için O(1) lookup yapılır. Toplam maliyet O(n + m).
    // Bonus: map.get(id) null dönerse "bu ürün restoranda yok" durumu artık GÖRÜNÜR ve reddediliyor
    // → [[Nested Loop - HashMap Optimizasyonu]] notuna bak.
    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        Map<ProductId, Product> restaurantProducts = restaurant.getProducts().stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        order.getItems().forEach(orderItem -> {
            Product restaurantProduct = restaurantProducts.get(orderItem.getProduct().getId());
            if (restaurantProduct == null) {
                throw new OrderDomainException("Product with id: " + orderItem.getProduct().getId().getValue()
                        + " is not found in restaurant with id: " + restaurant.getId().getValue());
            }
            orderItem.getProduct().updateWithConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
        });
    }
}
