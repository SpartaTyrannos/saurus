package com.example.saurus;

import com.example.saurus.domain.order.dto.request.OrderCreateRequestDto;
import com.example.saurus.domain.order.service.OrderService;
import com.example.saurus.domain.payment.PaymentMethod;
import com.example.saurus.domain.section.entity.Section;
import com.example.saurus.domain.section.repository.SectionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SectionRepository sectionRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(60);

    @Test
    public void testConcurrentSeatBooking() throws InterruptedException {
        Long gameId = 1L;
        Long sectionId = 1L;
        Long userId = 1L;

        Section section = sectionRepository.findById(sectionId).orElseThrow();
        section.setCount(120);
        sectionRepository.save(section);

        int requestCount = 100;
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    OrderCreateRequestDto request = new OrderCreateRequestDto();
                    request.setGameId(gameId);
                    request.setSectionId(sectionId);
                    request.setSeatCount(2);
                    request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

                    orderService.createOrder(request, userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Section updatedSection = sectionRepository.findById(sectionId).orElseThrow();

        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failureCount.get());
        Assertions.assertEquals(0, updatedSection.getCount());
    }

}

