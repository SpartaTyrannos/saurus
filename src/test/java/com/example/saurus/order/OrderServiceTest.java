package com.example.saurus.order;

import com.example.saurus.domain.order.dto.request.OrderCreateRequestDto;
import com.example.saurus.domain.order.dto.response.CancelOrderResponseDto;
import com.example.saurus.domain.order.dto.response.OrderListResponseDto;
import com.example.saurus.domain.order.dto.response.OrderResponseDto;
import com.example.saurus.domain.order.repository.OrderRepository;
import com.example.saurus.domain.order.service.OrderService;
import com.example.saurus.domain.payment.PaymentMethod;
import com.example.saurus.domain.seat.entity.Seat;

import com.example.saurus.domain.seat.enums.SeatType;
import com.example.saurus.domain.seat.repository.SeatRepository;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Long testUserId;


    private Long seatId1;
    private Long seatId2;
    private Long seatId3;

    @BeforeEach
    public void setup() {

        User user = new User();
        user.setName("Test User");
        user = userRepository.save(user);
        testUserId = user.getId();

        Seat seat1 = new Seat();
        seat1.setSeatRow("A");
        seat1.setNumber("101");
        seat1.setSeatType(SeatType.RED);

        seat1 = seatRepository.save(seat1);
        seatId1 = seat1.getId();

        Seat seat2 = new Seat();
        seat2.setSeatRow("A");
        seat2.setNumber("102");
        seat2.setSeatType(SeatType.RED);
        seat2 = seatRepository.save(seat2);
        seatId2 = seat2.getId();

        Seat seat3 = new Seat();
        seat3.setSeatRow("B");
        seat3.setNumber("201");
        seat3.setSeatType(SeatType.RED);
        seat3 = seatRepository.save(seat3);
        seatId3 = seat3.getId();


    }

    @Test
    public void testCreateOrder() {

        OrderCreateRequestDto request = new OrderCreateRequestDto();
        request.setGameId(100L);
        request.setSeatIdList(Arrays.asList(seatId1, seatId2));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);


        OrderResponseDto response = orderService.createOrder(request, testUserId);


        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(testUserId);
        assertThat(response.getTicketCount()).isEqualTo(2);

        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    public void testGetOrder() {

        OrderCreateRequestDto request = new OrderCreateRequestDto();
        request.setGameId(100L);
        request.setSeatIdList(Arrays.asList(seatId1));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        OrderResponseDto created = orderService.createOrder(request, testUserId);

        // 생성된 주문 조회
        OrderResponseDto fetched = orderService.getOrder(testUserId, created.getOrderId());
        assertThat(fetched).isNotNull();
        assertThat(fetched.getOrderId()).isEqualTo(created.getOrderId());
        assertThat(fetched.getTicketCount()).isEqualTo(created.getTicketCount());
    }

    @Test
    public void testGetOrders() {

        for (int i = 0; i < 3; i++) {
            OrderCreateRequestDto request = new OrderCreateRequestDto();
            request.setGameId(100L);

            Long seatToUse = (i % 2 == 0) ? seatId1 : seatId3;
            request.setSeatIdList(Arrays.asList(seatToUse));
            request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
            orderService.createOrder(request, testUserId);
        }


        Pageable pageable = PageRequest.of(0, 10);
        OrderListResponseDto listResponse = orderService.getOrders(testUserId, pageable);


        assertThat(listResponse).isNotNull();
        assertThat(listResponse.getContents()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(listResponse.getMeta().getPage()).isEqualTo(0);
        assertThat(listResponse.getMeta().getPageSize()).isEqualTo(10);
    }

    @Test
    public void testCancelOrder() {

        OrderCreateRequestDto request = new OrderCreateRequestDto();
        request.setGameId(100L);
        request.setSeatIdList(Arrays.asList(seatId1, seatId2));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        OrderResponseDto created = orderService.createOrder(request, testUserId);


        CancelOrderResponseDto cancelResponse = orderService.cancelOrder(testUserId, created.getOrderId());


        assertThat(cancelResponse).isNotNull();
        assertThat(cancelResponse.getTotalPrice()).isGreaterThan(0);

    }

    @Test
    public void testCreateOrder_InvalidSeat() {

        OrderCreateRequestDto request = new OrderCreateRequestDto();
        request.setGameId(100L);
        request.setSeatIdList(Arrays.asList(9999L));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(request, testUserId);
        });
    }
}
