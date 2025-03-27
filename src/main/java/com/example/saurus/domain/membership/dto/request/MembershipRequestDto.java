package com.example.saurus.domain.membership.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequestDto {

    @NotBlank(message = "멤버십 이름은 필수값입니다.")
    @Size(max = 50, message = "멤버십 이름은 최대 50자 입니다.")
    private String name;

    @NotNull(message = "멤버십 가격은 필수 값입니다.")
    private Integer price;

    @NotNull(message = "멤버십 할인율은 필수 값입니다.")
    @Min(value = 0, message = "할인율은 0(0%) 이상이어야 합니다.")
    @Max(value = 1, message = "할인율은 1(100%) 이하여야 합니다.")
    private Double discount;

    @NotNull(message = "멤버십 연도는 필수 값입니다.")
    private Integer year;

}
