package com.nexters.goalpanzi.config.jwt;

import com.sun.istack.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record Jwt(
        @NotEmpty String accessToken,
        @NotEmpty String refreshToken,
        @NotNull Long refreshExpiresIn) {
}
