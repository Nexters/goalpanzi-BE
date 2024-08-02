package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.MyMissionVerificationCommand;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.ncp.ObjectStorageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionRepository missionRepository;
    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MemberRepository memberRepository;

    private final ObjectStorageManager objectStorageManager;

    @Transactional(readOnly = true)
    public List<MissionVerificationResponse> getVerifications(final MissionVerificationCommand command) {
        LocalDate date = command.date() != null ? command.date() : LocalDate.now();
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(command.missionId());
        List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(command.missionId(), date);

        Map<Long, MissionVerification> verificationMap = verifications.stream()
                .collect(Collectors.toMap(v -> v.getMember().getId(), v -> v));

        return missionMembers.stream()
                .map(m -> {
                    MissionVerification v = verificationMap.get(m.getMember().getId());
                    return v != null
                            ? toMissionVerificationResponse(true, m.getMember(), v)
                            : toMissionVerificationResponse(false, m.getMember(), null);
                })
                .sorted(Comparator.comparing((MissionVerificationResponse r) -> r.nickname().equals(member.getNickname())).reversed()
                        .thenComparing(MissionVerificationResponse::isVerified).reversed()
                        .thenComparing(MissionVerificationResponse::verifiedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MissionVerificationResponse getMyVerification(final MyMissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        MissionVerification verification =
                missionVerificationRepository.findByMemberIdAndMissionIdAndBoardNumber(command.memberId(), command.missionId(), command.number())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        return toMissionVerificationResponse(true, member, verification);
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        Member member =
                memberRepository.findById(command.memberId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
        Mission mission =
                missionRepository.findById(command.missionId())
                        .orElseThrow(() -> new NotFoundException("TODO"));
        MissionMember missionMember =
                missionMemberRepository.findByMemberIdAndMissionId(command.memberId(), command.missionId())
                        .orElseThrow(() -> new NotFoundException("TODO"));

        checkVerificationValidation(command.memberId(), command.missionId(), mission, missionMember);

        String imageUrl = objectStorageManager.uploadFile(command.imageFile());
        missionVerificationRepository.save(new MissionVerification(member, mission, imageUrl));
        missionMember.verify();
    }

    private MissionVerificationResponse toMissionVerificationResponse(Boolean isVerified, Member member, MissionVerification verification) {
        return new MissionVerificationResponse(
                isVerified,
                member.getNickname(),
                isVerified ? verification.getImageUrl() : "",
                isVerified ? verification.getCreatedAt() : null
        );
    }

    private void checkVerificationValidation(final Long memberId, final Long missionId, final Mission mission, final MissionMember missionMember) {
        if (isCompletedMission(mission, missionMember)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
        if (isDuplicatedVerification(memberId, missionId)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
    }

    private boolean isCompletedMission(final Mission mission, final MissionMember missionMember) {
        return missionMember.getVerificationCount() >= mission.getBoardCount();
    }

    private boolean isDuplicatedVerification(final Long memberId, final Long missionId) {
        LocalDate today = LocalDateTime.now().toLocalDate();
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, today).isPresent();
    }
}
