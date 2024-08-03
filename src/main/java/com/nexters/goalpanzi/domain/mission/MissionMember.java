package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mission_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MissionMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "verification_count")
    private Integer verificationCount;

    public MissionMember(final Member member, final Mission mission, final Integer verificationCount) {
        this.member = member;
        this.mission = mission;
        this.verificationCount = verificationCount;
    }

    public static MissionMember join(final Member member, final Mission mission) {
        return new MissionMember(member, mission, 0);
    }

    public void verify() {
        this.verificationCount++;
    }
}
