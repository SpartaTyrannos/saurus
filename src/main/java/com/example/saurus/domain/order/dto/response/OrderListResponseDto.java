package com.example.saurus.domain.order.dto.response;

import com.example.saurus.domain.order.dto.MetaDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderListResponseDto {

    private List<OrderResponseDto> contents;
    private MetaDto meta;

}
