package com.example.saurus.domain.order.controller;

import com.example.saurus.domain.common.annotation.User;

import com.example.saurus.domain.common.dto.AuthUser;


import com.example.saurus.domain.order.dto.request.OrderCreateRequestDto;
import com.example.saurus.domain.order.dto.response.CancelOrderResponseDto;
import com.example.saurus.domain.order.dto.response.OrderListResponseDto;
import com.example.saurus.domain.order.dto.response.OrderResponseDto;
import com.example.saurus.domain.order.service.OrderService;
import com.example.saurus.domain.payment.dto.request.PaymentMethodRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @User
    @PostMapping("/api/v1/orders")
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam("gameId") Long gameId,
            @RequestParam("seatIds") List<Long> seatIds,
            @RequestBody PaymentMethodRequest methodRequest
    ) {

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto();
        requestDto.setGameId(gameId);
        requestDto.setSeatIdList(seatIds);
        requestDto.setPaymentMethod(methodRequest.getPaymentMethod());

        Long userId = authUser.getId();

        OrderResponseDto response = orderService.createOrder(requestDto, userId);
        return ResponseEntity.ok(response);
    }


    // 주문 다건 조회
    @User
    @GetMapping("/api/v1/my/orders")
    public ResponseEntity<OrderListResponseDto> getOrders(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long userId = authUser.getId();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        OrderListResponseDto response = orderService.getOrders(userId, pageable);
        return ResponseEntity.ok(response);
    }

   // 주문 단건 조회
    @User
    @GetMapping("/api/v1/my/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderResponseDto response = orderService.getOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }

    // 주문 취소
    @User
    @DeleteMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<CancelOrderResponseDto> cancelOrder(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        CancelOrderResponseDto response = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }
}
