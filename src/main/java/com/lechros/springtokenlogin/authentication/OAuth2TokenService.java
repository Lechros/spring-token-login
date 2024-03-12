package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.data.AccessTokenResponse;
import com.lechros.springtokenlogin.token.TokenContext;
import com.lechros.springtokenlogin.token.TokenGenerator;
import com.lechros.springtokenlogin.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuth2TokenService {

    @Value("${authorizationserver.issuer}")
    private String issuer;

    @Value("${authorizationserver.audience}")
    private String audience;

    @Value("${authorizationserver.access-token-time-to-live}")
    private Long accessTokenTimeToLive;

    @Value("${authorizationserver.refresh-token-time-to-live}")
    private Long refreshTokenTimeToLive;

    private final TokenGenerator tokenGenerator;

    public AccessTokenResponse createTokenResponse(Long userId) {
        TokenContext accessTokenContext = getAccessTokenContext(userId);
        TokenContext refreshTokenContext = getRefreshTokenContext(userId);

        Jwt accessToken = tokenGenerator.generateAccessToken(accessTokenContext);
        OAuth2RefreshToken refreshToken = tokenGenerator.generateRefreshToken(refreshTokenContext);

        return AccessTokenResponse.withToken(accessToken).refreshToken(refreshToken).build();
    }

    private TokenContext getAccessTokenContext(Long userId) {
        return TokenContext.builder()
            .tokenType(TokenType.ACCESS_TOKEN)
            .issuer(issuer)
            .audience(audience)
            .tokenTimeToLive(Duration.ofSeconds(accessTokenTimeToLive))
            .userId(userId)
            .authorizedScopes(Collections.emptySet())
            .build();
    }

    private TokenContext getRefreshTokenContext(Long userId) {
        return TokenContext.builder()
            .tokenType(TokenType.REFRESH_TOKEN)
            .issuer(issuer)
            .audience(audience)
            .tokenTimeToLive(Duration.ofSeconds(refreshTokenTimeToLive))
            .userId(userId)
            .authorizedScopes(Collections.emptySet())
            .build();
    }

}
