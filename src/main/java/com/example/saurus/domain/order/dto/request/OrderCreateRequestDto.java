package com.example.saurus.domain.order.dto.request;

import com.example.saurus.domain.payment.PaymentMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequestDto {

    @NotNull(message = "gameId는 필수입니다.")
    private Long gameId;

    @NotNull(message = "sectionId는 필수입니다.")
    private Long sectionId;

    @NotNull(message = "결제수단 선택은 필수입니다.")
    private PaymentMethod paymentMethod;

    @NotNull(message = "좌석 개수는 필수입니다.")
    @Min(value = 1, message = "좌석 개수는 1개 이상이어야 합니다.")
    @Max(value = 4, message = "좌석 개수는 4개 이하여야 합니다.")
    private Integer seatCount;

}
