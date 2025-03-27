package com.example.saurus.domain.order.dto.request;

import com.example.saurus.domain.payment.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequestDto {

    private Long gameId;
    private List<Long> seatIdList;

    private PaymentMethod paymentMethod;

}
