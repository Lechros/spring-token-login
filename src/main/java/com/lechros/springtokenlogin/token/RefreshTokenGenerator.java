package com.lechros.springtokenlogin.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RefreshTokenGenerator implements TokenGenerator<OAuth2RefreshToken> {

    private final StringKeyGenerator tokenGenerator =
        new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    public OAuth2RefreshToken generate(TokenParams params) {
        if (!TokenType.REFRESH_TOKEN.equals(params.getTokenType())) {
            return null;
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(params.getTokenTimeToLive());

        return new OAuth2RefreshToken(tokenGenerator.generateKey(), issuedAt, expiresAt);
    }
}
