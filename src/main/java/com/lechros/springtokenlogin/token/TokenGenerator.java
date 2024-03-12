package com.lechros.springtokenlogin.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private final JwtEncoder jwtEncoder;
    private final StringKeyGenerator refreshTokenGenerator =
        new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    public Jwt generateAccessToken(TokenContext context) {
        if (!TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            return null;
        }

        String issuer = context.getIssuer();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(context.getTokenTimeToLive());

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }
        claimsBuilder
            .subject(String.valueOf(context.getUserId()))
            .audience(Collections.singletonList(context.getAudience()))
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .notBefore(issuedAt);
        if (CollectionUtils.isEmpty(context.getAuthorizedScopes())) {
            claimsBuilder.claim(OAuth2ParameterNames.SCOPE, context.getAuthorizedScopes());
        }

        JwtClaimsSet claims = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public OAuth2RefreshToken generateRefreshToken(TokenContext context) {
        if (!TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
            return null;
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(context.getTokenTimeToLive());
        return new OAuth2RefreshToken(refreshTokenGenerator.generateKey(), issuedAt, expiresAt);
    }
}
