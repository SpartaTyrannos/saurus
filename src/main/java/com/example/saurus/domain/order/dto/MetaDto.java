package com.example.saurus.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// 페에징 메타 Data
@Getter
@Setter
@AllArgsConstructor
public class MetaDto {

    private Long totalCount;
    private int page;
    private int pageSize;

}
