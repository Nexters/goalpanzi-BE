package com.nexters.goalpanzi.domain.member.repository;

import com.nexters.goalpanzi.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findByNickname(String nickname);
}