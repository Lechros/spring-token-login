package com.lechros.springtokenlogin.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;

@RequiredArgsConstructor
public class AccessTokenGenerator {

    private final JwtEncoder jwtEncoder;

    public Jwt generate(TokenParams params) {
        if (!TokenType.ACCESS_TOKEN.equals(params.getTokenType())) {
            return null;
        }

        String issuer = params.getIssuer();

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(params.getTokenTimeToLive());

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        if (StringUtils.hasText(issuer)) {
            claimsBuilder.issuer(issuer);
        }
        claimsBuilder
            .subject(params.getUserId())
            .audience(Collections.singletonList(params.getAudience()))
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .notBefore(issuedAt);

        JwtClaimsSet claims = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }
}
