package com.example.saurus.domain.payment.dto.request;

import com.example.saurus.domain.payment.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodRequest {

    private PaymentMethod paymentMethod;

}
