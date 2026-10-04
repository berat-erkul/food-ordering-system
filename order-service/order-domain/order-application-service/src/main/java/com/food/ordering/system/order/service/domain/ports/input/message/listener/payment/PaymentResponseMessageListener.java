package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

// "Driving Adapter" - call by the "PAYMENT SERVİCE --> KAFKA"

public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse); //invariant failure or cancel request from: Restaurant -> Order -> Payment

}
