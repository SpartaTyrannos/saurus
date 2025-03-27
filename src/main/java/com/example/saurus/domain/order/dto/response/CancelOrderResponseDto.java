package com.example.saurus.domain.order.dto.response;

import com.example.saurus.domain.payment.PaymentMethod;
import com.example.saurus.domain.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancelOrderResponseDto {

    private int totalPrice;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;


}
