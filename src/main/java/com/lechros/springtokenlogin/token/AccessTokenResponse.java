package com.lechros.springtokenlogin.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessTokenResponse {

    private String tokenType;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;

    public static Builder withToken(AbstractOAuth2Token accessToken) {
        return new Builder().accessToken(accessToken);
    }

    public static class Builder {

        private String tokenType;
        private String accessToken;
        private Long expiresIn;
        private String refreshToken;
        private Long refreshTokenExpiresIn;

        Builder accessToken(AbstractOAuth2Token token) {
            tokenType = OAuth2AccessToken.TokenType.BEARER.getValue();
            accessToken = token.getTokenValue();
            expiresIn = Instant.now().until(token.getExpiresAt(), ChronoUnit.SECONDS);
            return this;
        }

        public Builder refreshToken(AbstractOAuth2Token token) {
            refreshToken = token.getTokenValue();
            refreshTokenExpiresIn = Instant.now().until(token.getExpiresAt(), ChronoUnit.SECONDS);
            return this;
        }

        public AccessTokenResponse build() {
            return new AccessTokenResponse(tokenType, accessToken, expiresIn, refreshToken, refreshTokenExpiresIn);
        }
    }
}
