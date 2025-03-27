package com.example.saurus.domain.order.dto.response;

import com.example.saurus.domain.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private Long userId;
    private int ticketCount;
    private double discount;
    private int totalPrice;
    private String createdAt;
    private PaymentMethod paymentMethod;
}
