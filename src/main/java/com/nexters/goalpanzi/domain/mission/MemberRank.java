package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.member.Member;

public record MemberRank(
        Member member,
        Integer rank
) {
}
