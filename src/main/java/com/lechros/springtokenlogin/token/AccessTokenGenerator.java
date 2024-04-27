package com.lechros.springtokenlogin.token;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AccessTokenGenerator implements TokenGenerator<OAuth2AccessToken> {

    private final SecretKeySpec secretKey;

    public OAuth2AccessToken generate(TokenParams params) {
        if (!TokenType.ACCESS_TOKEN.equals(params.getTokenType())) {
            return null;
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(params.getTokenTimeToLive());

        String tokenValue = Jwts.builder()
            .issuer(params.getIssuer())
            .subject(params.getUserId())
            .audience().add(params.getAudiences()).and()
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(expiresAt))
            .notBefore(Date.from(issuedAt))
            .signWith(secretKey)
            .compact();

        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, issuedAt, expiresAt);
    }
}
