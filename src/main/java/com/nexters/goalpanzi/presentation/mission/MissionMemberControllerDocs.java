package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.response.MemberRankResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "미션/회원")
public interface MissionMemberControllerDocs {

    @Operation(summary = "내가 참여한 미션 조회")
    ResponseEntity<MissionsResponse> getMissions(@Parameter(hidden = true) @LoginMemberId final Long memberId);

    @Operation(summary = "미션 참여", description = "초대코드로 미션에 참여합니다.")
    ResponseEntity<Void> joinMission(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @RequestBody JoinMissionRequest request
    );

    @Operation(summary = "내 미션 최종 순위 조회")
    ResponseEntity<MemberRankResponse> getMissionRank(
            @RequestParam final Long missionId,
            @Parameter(hidden = true) @LoginMemberId final Long memberId
    );
}
