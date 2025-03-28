package com.example.saurus.domain.membership.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.membership.dto.request.MembershipRequestDto;
import com.example.saurus.domain.membership.dto.response.MembershipResponseDto;
import com.example.saurus.domain.membership.entity.Membership;
import com.example.saurus.domain.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    @Transactional  // 관리자 권한
    public String saveMembership(MembershipRequestDto request) {
        if (membershipRepository.existsByName(request.getName())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 멤버십 이름이 이미 존재 합니다.");
        }

        Membership membership = new Membership(request.getName(), request.getPrice(), request.getDiscount(), request.getYear());
        membershipRepository.save(membership);

        return "멤버십이 생성되었습니다.";
    }

    @Transactional(readOnly = true)
    public Page<MembershipResponseDto> getMemberships(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<Membership> memberships = membershipRepository.findAll(pageable);

        return memberships.map(membership -> new MembershipResponseDto(
                membership.getId(),
                membership.getName(),
                membership.getPrice(),
                membership.getDiscount(),
                membership.getYear()
        ));
    }

    @Transactional(readOnly = true)
    public MembershipResponseDto getMembership(Long membershipId) {
        Membership membership = findMembershipById(membershipId);

        return new MembershipResponseDto(
                membership.getId(),
                membership.getName(),
                membership.getPrice(),
                membership.getDiscount(),
                membership.getYear()
        );
    }

    @Transactional  // 관리자 권한
    public String updateMembership(Long membershipId, MembershipRequestDto request) {
        if (membershipRepository.existsByName(request.getName())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 멤버십 이름이 이미 존재 합니다.");
        }

        Membership membership = findMembershipById(membershipId);

        membership.update(request.getName(), request.getPrice(), request.getDiscount(), request.getYear());

        return "멤버십이 수정되었습니다.";
    }

    public Membership findMembershipById(Long membershipId) {
        return membershipRepository.findById(membershipId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "멤버십을 찾을 수 없습니다."));
    }
}
