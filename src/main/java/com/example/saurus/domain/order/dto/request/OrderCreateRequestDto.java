package com.example.saurus.domain.order.dto.request;

import com.example.saurus.domain.payment.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequestDto {

    @NotNull(message = "gameId는 필수입니다.")
    private Long gameId;

    @NotEmpty(message = "최소 한 개 이상의 좌석 ID가 필요합니다.")
    private List<Long> seatIdList;

    @NotNull(message = "결제수단 선택은 필수입니다.")
    private PaymentMethod paymentMethod;

}
