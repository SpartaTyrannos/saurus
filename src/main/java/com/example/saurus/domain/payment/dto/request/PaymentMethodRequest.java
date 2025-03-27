package com.example.saurus.domain.payment.dto.request;

import com.example.saurus.domain.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodRequest {

    @NotNull(message = "결제수단 선택은 필수입니다.")
    private PaymentMethod paymentMethod;

}
