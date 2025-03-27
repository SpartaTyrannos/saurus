package com.example.saurus.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCancelRequestDto {

    @NotNull(message = "orderId는 필수입니다.")
    private Long orderId;
}
