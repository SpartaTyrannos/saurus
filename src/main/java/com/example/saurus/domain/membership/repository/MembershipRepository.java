package com.example.saurus.domain.membership.repository;

import com.example.saurus.domain.membership.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    boolean existsByName(String name);

}
