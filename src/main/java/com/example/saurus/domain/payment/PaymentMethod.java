package com.example.saurus.domain.payment;

public enum PaymentMethod {

    CREDIT_CARD("신용카드"),
    DEBIT_CARD("체크 카드"),
    BANK_TRANSFER("계좌 이체"),
    MOBILE_PAYMENT("모바일 결제");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}
