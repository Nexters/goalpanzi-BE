package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.fixture.MissionFixture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberRanksTest {

    @Test
    void 미션_최종_등수를_확인할_수_있다() {
        // given
        Member memberHost = Member.socialLogin(SOCIAL_ID, EMAIL_HOST, SocialType.APPLE);
        Member memberA = Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.APPLE);
        Member memberB = Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_B, SocialType.GOOGLE);

        List<MissionMember> missionMembers = List.of(
                new MissionMember(memberHost, MissionFixture.create(), 10),
                new MissionMember(memberA, MissionFixture.create(), 12),
                new MissionMember(memberB, MissionFixture.create(), 14)
        );

        // when, then
        MemberRanks actual = MemberRanks.from(missionMembers);
        assertAll(
                () -> assertThat(actual.getRankByMember(memberB).rank()).isEqualTo(1),
                () -> assertThat(actual.getRankByMember(memberA).rank()).isEqualTo(2),
                () -> assertThat(actual.getRankByMember(memberHost).rank()).isEqualTo(3)
        );
    }
}