package com.nexters.goalpanzi.application.auth;

import com.nexters.goalpanzi.application.auth.dto.AppleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.LoginResponse;
import com.nexters.goalpanzi.application.auth.dto.TokenResponse;
import com.nexters.goalpanzi.common.jwt.Jwt;
import com.nexters.goalpanzi.common.jwt.JwtProvider;
import com.nexters.goalpanzi.domain.auth.repository.RefreshTokenRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SocialUserProviderFactory socialUserProviderFactory;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public LoginResponse appleOAuthLogin(final AppleLoginCommand command) {
        SocialUserProvider appleUserProvider = socialUserProviderFactory.getProvider(SocialType.APPLE);
        SocialUserInfo socialUserInfo = appleUserProvider.getSocialUserInfo(command.identityToken());

        return socialLogin(socialUserInfo, SocialType.APPLE);
    }

    public LoginResponse googleOAuthLogin(final GoogleLoginCommand command) {
        SocialUserInfo socialUserInfo = new SocialUserInfo(command.identityToken(), command.email());

        return socialLogin(socialUserInfo, SocialType.GOOGLE);
    }

    private LoginResponse socialLogin(final SocialUserInfo socialUserInfo, final SocialType socialType) {
        Member member = memberRepository.findBySocialId(socialUserInfo.socialId())
                .orElseGet(() ->
                        memberRepository.save(Member.socialLogin(socialUserInfo.socialId(), socialUserInfo.email(), socialType))
                );

        Jwt jwt = jwtProvider.generateTokens(member.getId().toString());
        refreshTokenRepository.save(member.getId().toString(), jwt.refreshToken(), jwt.refreshExpiresIn());

        return new LoginResponse(jwt.accessToken(), jwt.refreshToken(), member.isProfileSet());
    }

    public void logout(final String altKey) {
        refreshTokenRepository.delete(altKey);
    }

    public TokenResponse reissueToken(final String altKey, final String refreshToken) {
        validateRefreshToken(altKey, refreshToken);

        Jwt jwt = jwtProvider.generateTokens(altKey);
        refreshTokenRepository.save(altKey, jwt.refreshToken(), jwt.refreshExpiresIn());

        return new TokenResponse(jwt.accessToken(), jwt.refreshToken());
    }

    private void validateRefreshToken(final String altKey, final String refreshToken) {
        String storedRefreshToken = refreshTokenRepository.find(altKey);

        if (!refreshToken.equals(storedRefreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
    }
}