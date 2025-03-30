//package com.example.saurus.domain.seat.dto.request;
//
//import com.example.saurus.domain.seat.enums.SeatType;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class SeatCreateRequest {
//
//    @NotBlank(message = "행(row)은 필수입니다.")
//    private String seatRow;
//
//    @NotNull(message = "좌석 번호는 필수입니다.")
//    @Min(value = 1, message = "좌석 번호는 1 이상이어야 합니다.")
//    private Integer number;
//
//    @NotNull(message = "좌석 타입은 필수입니다.")
//    private SeatType seatType;
//}
