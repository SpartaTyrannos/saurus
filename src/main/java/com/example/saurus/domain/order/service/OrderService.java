package com.example.saurus.domain.order.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.order.OrderStatus;
import com.example.saurus.domain.order.dto.MetaDto;
import com.example.saurus.domain.order.dto.request.OrderCreateRequestDto;
import com.example.saurus.domain.order.dto.response.CancelOrderResponseDto;
import com.example.saurus.domain.order.dto.response.OrderListResponseDto;
import com.example.saurus.domain.order.dto.response.OrderResponseDto;
import com.example.saurus.domain.order.entity.Order;
import com.example.saurus.domain.order.repository.OrderRepository;
import com.example.saurus.domain.payment.PaymentStatus;
import com.example.saurus.domain.payment.entity.Payment;
import com.example.saurus.domain.payment.repository.PaymentRepository;
import com.example.saurus.domain.seat.entity.Seat;
import com.example.saurus.domain.seat.repository.SeatRepository;
import com.example.saurus.domain.subscribe.entity.Subscribe;
import com.example.saurus.domain.subscribe.repository.SubscribeRepository;
import com.example.saurus.domain.ticket.entity.Ticket;
import com.example.saurus.domain.ticket.repository.TicketRepository;
import com.example.saurus.domain.user.entity.User;
import com.example.saurus.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final SubscribeRepository subscribeRepository;

    /**
     * 주문 생성 (주문, 티켓, 결제를 한 트랜잭션 내 처리)
     *
     * @param request 주문 생성 요청 DTO (gameId, seatIds)
     * @param userId  JWT에서 추출된 사용자 ID
     * @return 생성된 주문에 대한 응답 DTO
     */
    public OrderResponseDto createOrder(OrderCreateRequestDto request, Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다."));

        if (request.getSeatIdList() == null || request.getSeatIdList().isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND,"좌석이 존재하지 않습니다.");
        }

        if (request.getSeatIdList().size() > 4) {
            throw new CustomException(HttpStatus.BAD_REQUEST,"1인당 최대 4장까지만 예매가 가능합니다.");
        }

        /*
         * 좌석 조회
         * 동시성 제어 필요
         * */
        List<Seat> seats = seatRepository.findAllById(request.getSeatIdList());
        if (seats.size() != request.getSeatIdList().size()) {
            throw new CustomException(HttpStatus.BAD_REQUEST,"하나 이상의 좌석을 찾을 수 없습니다.");
        }

        // 좌석 별로 해당 경기의 좌석이 맞는지 확인
        for (Seat seat : seats) {
            Long gameSeatId = seat.getSection().getGame().getId();
            if (!request.getGameId().equals(gameSeatId)) {
                throw new CustomException(HttpStatus.BAD_REQUEST,"해당 경기 좌석이 아닙니다.");
            }
        }

        // 주문 생성
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(0);
        orderRepository.save(order);

        int totalTicketPrice = 0;
        int ticketCount = 0;

        // 티켓 생성
        for (Seat seat : seats) {
            int seatPrice = seat.getSection().getPrice();
            Ticket ticket = new Ticket();
            ticket.setOrder(order);
            ticket.setSeat(seat);
            ticketRepository.save(ticket);

            totalTicketPrice += seatPrice;
            ticketCount++;
        }

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        // DB에서 유효한 구독만 조회
        List<Subscribe> activeSubscriptions = subscribeRepository.findActiveSubscriptions(userId, now);

        // 가장 최신 구독의 할인율 가져오기 (없으면 0.0)
        double discountRate = activeSubscriptions.stream().findFirst().map(sub -> sub.getMembership().getDiscount()).orElse(0.0);

        int discountedPrice = (int) (totalTicketPrice * (1 - discountRate));
        order.setTotalPrice(discountedPrice);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setFinalPrice(discountedPrice);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        // PG 연동 하여 결제 요청 및 결과 처리 로직 필요

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return new OrderResponseDto(
                order.getId(),
                userId,
                ticketCount,
                discountRate,
                discountedPrice,
                order.getCreatedAt().toString(),
                payment.getPaymentMethod());
    }

    // 주문 전체 조회 (페이징)
    @Transactional(readOnly = true)
    public OrderListResponseDto getOrders(Long userId, Pageable pageable) {

        LocalDateTime now = LocalDateTime.now();

        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        List<OrderResponseDto> contents = orderPage.getContent().stream()
                .map(order -> {
                    int ticketCount = order.getTickets().size();
                    double discountRate = subscribeRepository.findAll().stream()
                            .filter(s -> s.getUser().getId().equals(userId))
                            .filter(s -> !now.isBefore(s.getStartDate()) && !now.isAfter(s.getEndDate()))
                            .findFirst()
                            .map(s -> s.getMembership().getDiscount())
                            .orElse(0.0);

                    return new OrderResponseDto(
                            order.getId(),
                            userId,
                            ticketCount,
                            discountRate,
                            order.getTotalPrice(),
                            order.getCreatedAt().toString(),
                            order.getPayment().getPaymentMethod()
                    );
                }).collect(Collectors.toList());

        MetaDto meta = new MetaDto(orderPage.getTotalElements(), orderPage.getNumber(), orderPage.getSize());
        OrderListResponseDto listResponse = new OrderListResponseDto();
        listResponse.setContents(contents);
        listResponse.setMeta(meta);
        return listResponse;
    }

    // 주문 단건 조회
    public OrderResponseDto getOrder(Long userId, Long orderId) {

        Order order = orderRepository.findOrderWithTicketsById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"해당 주문이 없습니다."));

        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST,"해당 주문은 사용자의 주문이 아닙니다.");
        }

        int ticketCount = order.getTickets().size();
        LocalDateTime now = LocalDateTime.now();
        double discountRate = subscribeRepository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(userId))
                .filter(s -> !now.isBefore(s.getStartDate()) && !now.isAfter(s.getEndDate()))
                .findFirst()
                .map(s -> s.getMembership().getDiscount())
                .orElse(0.0);

        return new OrderResponseDto(
                order.getId(),
                userId,
                ticketCount,
                discountRate,
                order.getTotalPrice(),
                order.getCreatedAt().toString(),
                order.getPayment().getPaymentMethod()
        );
    }


    // 주문 취소 -> 주문,티켓,결제 모두 한 트랙잭션 내에 처리
    public CancelOrderResponseDto cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"존재하지 않는 주문입니다."));

        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST,"해당 주문은 사용자의 주문이 아닙니다.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new CustomException(HttpStatus.BAD_REQUEST,"이미 취소된 주문입니다.");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"결제 정보가 존재하지 않습니다."));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {

            // 실제 환불 로직 필요

            payment.setPaymentStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        List<Ticket> tickets = ticketRepository.findAllByOrder(order);
        ticketRepository.deleteAll(tickets);

        return new CancelOrderResponseDto(order.getTotalPrice(), payment.getPaymentMethod(), payment.getPaymentStatus());
    }
}
