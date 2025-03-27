package com.example.saurus.domain.order;

public enum OrderStatus {

    CREATED("주문 생성"),
    PAID("결제 완료"),
    CANCELLED("주문 취소"),
    COMPLETED("주문 완료"),
    REFUND_REQUESTED("환불 요청"),
    REFUND_REFUSED("환불 거절"),
    REFUNDED("환불 완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
