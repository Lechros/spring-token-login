package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.data.AccessTokenResponse;
import com.lechros.springtokenlogin.domain.RefreshToken;
import com.lechros.springtokenlogin.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenAuthenticationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2TokenService oauth2TokenService;

    public AccessTokenResponse refresh(String grantType, String refreshToken, Long userId) {
        if (!OAuth2ParameterNames.REFRESH_TOKEN.equals(grantType)) {
            throw new RuntimeException("Invalid grant type");
        }
        Long tokenUserId = getRefreshTokenUserId(refreshToken);
        if (!tokenUserId.equals(userId)) {
            throw new RuntimeException("Invalid refresh token");
        }
//        return oauth2TokenService.createTokenResponse();
        return null;
    }

    private Long getRefreshTokenUserId(String token) {
        RefreshToken refreshToken = refreshTokenRepository
            .findByToken(token).orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!refreshToken.isValid(Instant.now().getEpochSecond())) {
            throw new RuntimeException("Invalid refresh token");
            // TODO : Do something if refresh token replay is detected
        }

        return refreshToken.getUserId();
    }
}
