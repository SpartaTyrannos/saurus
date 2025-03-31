package com.example.saurus.domain.order.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.game.entity.Game;
import com.example.saurus.domain.game.repository.GameRepository;
import com.example.saurus.domain.lock.service.RedisLockService;
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
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.repository.SectionRepository;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final SubscribeRepository subscribeRepository;
    private final GameRepository gameRepository;
    private final SectionRepository sectionRepository;
    private final RedisLockService redisLockService;  // Redis 기반 락 서비스

    /**
     * 주문 생성 (주문, 티켓, 결제를 한 트랜잭션 내 처리)
     *
     * @param request 주문 생성 요청 DTO (gameId, sectionId, seatCount, paymentMethod)
     * @param userId  JWT에서 추출된 사용자 ID
     * @return 생성된 주문에 대한 응답 DTO
     */
    public OrderResponseDto createOrder(OrderCreateRequestDto request, Long userId) {
        // 섹션별 락 키 생성 (예: "seat_lock:5" – 구역 ID가 5인 경우)
        String lockKey = "seat_lock:" + request.getSectionId();

        AtomicReference<OrderResponseDto> responseRef = new AtomicReference<>();

        // Redis 락을 이용하여 동시성 제어
        redisLockService.executeWithLock(lockKey, () -> {
            // 1. 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

            // 2. 경기 및 구역(Section) 조회
            Game game = gameRepository.findById(request.getGameId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 경기입니다."));
            Section section = sectionRepository.findWithGameById(request.getSectionId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 구역입니다."));

            // 3. 유효성 검사
            if (!game.getId().equals(section.getGame().getId())) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "해당 경기 구역이 아닙니다.");
            }
            if (section.getCount() < request.getSeatCount()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "남아있는 좌석 수 부족");
            }

            // 4. 좌석 수 차감 및 섹션 업데이트
            section.setCount(section.getCount() - request.getSeatCount());
            sectionRepository.save(section);

            // 5. 주문 생성
            Order order = new Order();
            order.setUser(user);
            order.setTicketAmount(request.getSeatCount());
            order.setTotalPrice(section.getPrice() * request.getSeatCount());
            order.setStatus(OrderStatus.CREATED);
            orderRepository.save(order);

            // 6. 티켓 생성 (요청한 좌석 수 만큼 티켓 생성)
            for (int i = 0; i < request.getSeatCount(); i++) {
                Ticket ticket = new Ticket(order, section);
                ticketRepository.save(ticket);
            }

            // 7. 구독 할인 계산
            LocalDateTime now = LocalDateTime.now();
            List<Subscribe> activeSubscriptions = subscribeRepository.findActiveSubscriptions(userId, now);
            double discountRate = activeSubscriptions.stream()
                    .findFirst()
                    .map(sub -> sub.getMembership().getDiscount())
                    .orElse(0.0);
            int discountedPrice = (int) (order.getTotalPrice() * (1 - discountRate));

            // 8. 결제 처리
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setFinalPrice(discountedPrice);
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            // 결제 성공 처리 (실제 결제 로직은 PG 연동 등으로 확장 가능)
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            // 9. 결과 저장 및 반환
            responseRef.set(new OrderResponseDto(
                    order.getId(),
                    userId,
                    request.getSeatCount(),
                    discountRate,
                    discountedPrice,
                    order.getCreatedAt().toString(),
                    request.getPaymentMethod(),
                    OrderStatus.PAID
            ));
        });
        return responseRef.get();
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
                            order.getPayment().getPaymentMethod(),
                            order.getStatus()
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
                order.getPayment().getPaymentMethod(),
                order.getStatus()
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
