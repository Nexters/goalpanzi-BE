package com.nexters.goalpanzi.presentation.member;

import com.nexters.goalpanzi.application.member.MemberService;
import com.nexters.goalpanzi.application.member.dto.ProfileRequest;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @Override
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @LoginMemberId final Long memberId,
            @RequestBody @Valid final ProfileRequest request
    ) {
        memberService.updateProfile(memberId, request);

        return ResponseEntity.ok().build();
    }
}
