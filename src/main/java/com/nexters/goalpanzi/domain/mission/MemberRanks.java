package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.member.Member;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
public class MemberRanks {

    private final List<MemberRank> memberRanks;

    public static MemberRanks from(final List<MissionMember> missionMembers) {
        List<MissionMember> sortedMissionMembers = sortedMembersByVerificationCountDesc(missionMembers);

        List<MemberRank> memberRanks = new ArrayList<>();
        int rank = 1;
        int previousVerificationCount = sortedMissionMembers.getFirst().getVerificationCount();

        for (int i = 0; i < sortedMissionMembers.size(); i++) {
            MissionMember missionMember = sortedMissionMembers.get(i);
            if (missionMember.getVerificationCount() < previousVerificationCount) {
                rank = i + 1;
            }
            memberRanks.add(new MemberRank(missionMember.getMember(), rank));
            previousVerificationCount = missionMember.getVerificationCount();
        }

        return new MemberRanks(memberRanks);
    }

    private static List<MissionMember> sortedMembersByVerificationCountDesc(final List<MissionMember> missionMembers) {
        return missionMembers
                .stream()
                .sorted((m1, m2) -> m2.getVerificationCount() - m1.getVerificationCount())
                .toList();
    }

    public MemberRank getRankByMember(final Member member) {
        return memberRanks.stream()
                .filter(memberRank -> memberRank.member().equals(member))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No rank found for member " + member.getId()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberRanks memberRanks1 = (MemberRanks) o;
        return Objects.equals(memberRanks, memberRanks1.memberRanks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(memberRanks);
    }

    @Override
    public String toString() {
        return "Ranks{" +
                "ranks=" + memberRanks +
                '}';
    }
}
