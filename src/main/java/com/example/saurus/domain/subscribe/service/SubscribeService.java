package com.example.saurus.domain.subscribe.service;

import com.example.saurus.domain.membership.entity.Membership;
import com.example.saurus.domain.membership.service.MembershipService;
import com.example.saurus.domain.subscribe.dto.response.SubscribeResponseDto;
import com.example.saurus.domain.subscribe.entity.Subscribe;
import com.example.saurus.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final MembershipService membershipService;
    private final SubscribeRepository subscribeRepository;

    @Transactional  // 유저 권한
    public String saveSubscribe(AuthUser authUser, Long membershipId) {
        User user = User.fromAuthUser(authUser);

        Membership membership = membershipService.findMembershipById(membershipId);

        if (membership.getYear() != Year.now().getValue()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 멤버십은 " + membership.getYear() + "년 전용입니다.");
        }

        if (subscribeRepository.existsByUserIdAndMembershipId(user.getId(), membershipId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 구독중인 멤버십입니다.");
        }

        Subscribe subscribe = new Subscribe(user, membership);
        subscribeRepository.save(subscribe);

        return "멤버십 구독이 완료 되었습니다.";
    }

    @Transactional(readOnly = true)  // 유저 권한
    public Page<SubscribeResponseDto> getSubscribes(AuthUser authUser, int page, int size) {
        User user = User.fromAuthUser(authUser);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<Subscribe> subscribes = subscribeRepository.findAllByUserId(user.getId(), pageable);

        return subscribes.map(subscribe -> new SubscribeResponseDto(
                subscribe.getId(),
                subscribe.getMembership().getId(),
                subscribe.getStartDate(),
                subscribe.getEndDate(),
                subscribe.isActive()
        ));
    }

    @Transactional  // 유저 권한
    public String deleteSubscribe(AuthUser authUser, Long subscribeId) {
        User user = User.fromAuthUser(authUser);

        Subscribe subscribe = findSubscribeById(subscribeId);

        if (!subscribe.isActive()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 만료된 구독입니다.");
        }

        if (!subscribe.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인의 구독만 해지할 수 있습니다.");
        }

        subscribe.delete();

        return "구독이 취소되었습니다.";
    }

    public Subscribe findSubscribeById(Long subscribeId) {
        return subscribeRepository.findWithUserById(subscribeId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "구독 정보를 찾을 수 없습니다."));
    }
}
