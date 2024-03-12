package com.lechros.springtokenlogin.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.time.Instant;
import java.util.Base64;

@RequiredArgsConstructor
public class RefreshTokenGenerator {

    private final StringKeyGenerator tokenGenerator =
        new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    public AbstractOAuth2Token generate(TokenParams params) {
        if (!TokenType.REFRESH_TOKEN.equals(params.getTokenType())) {
            return null;
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(params.getTokenTimeToLive());

        return new OAuth2RefreshToken(tokenGenerator.generateKey(), issuedAt, expiresAt);
    }
}
