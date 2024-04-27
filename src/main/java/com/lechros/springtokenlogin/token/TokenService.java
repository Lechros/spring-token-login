package com.lechros.springtokenlogin.token;

import com.lechros.springtokenlogin.config.TokenProperties;
import com.lechros.springtokenlogin.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProperties tokenProperties;

    private final TokenGenerator<?> accessTokenGenerator;
    private final TokenGenerator<?> refreshTokenGenerator;
    private final IssuedRefreshTokenRepository issuedRefreshTokenRepository;

    @Transactional
    public AccessTokenResponse issueNewAccessToken(User user) {
        return generateToken(user);
    }

    @Transactional
    public AccessTokenResponse refreshAccessToken(String refreshTokenValue) {
        Optional<IssuedRefreshToken> maybeFoundRefreshToken = issuedRefreshTokenRepository.findByTokenValue(refreshTokenValue);
        if (maybeFoundRefreshToken.isEmpty()) {
            throw new RuntimeException("Unknown refresh token");
        }
        IssuedRefreshToken foundRefreshToken = maybeFoundRefreshToken.get();
        if (foundRefreshToken.getRevoked()) {
            throw new RuntimeException("Revoked refresh token");
        }
        if (foundRefreshToken.isExpired(Instant.now().getEpochSecond())) {
            throw new RuntimeException("Expired refresh token");
        }

        User user = foundRefreshToken.getUser();

        // Refresh token rotation 사용, 기존 토큰 무효화
        foundRefreshToken.revoke();
        foundRefreshToken = issuedRefreshTokenRepository.save(foundRefreshToken);

        // 새로운 토큰 발급
        return generateToken(user);
    }

    private AccessTokenResponse generateToken(User user) {
        AbstractOAuth2Token accessToken = accessTokenGenerator.generate(accessTokenParams(user));
        AbstractOAuth2Token refreshToken = refreshTokenGenerator.generate(refreshTokenParams(user));

        IssuedRefreshToken issuedRefreshToken = IssuedRefreshToken.from(refreshToken, user);
        issuedRefreshToken = issuedRefreshTokenRepository.save(issuedRefreshToken);

        return AccessTokenResponse.withToken(accessToken).refreshToken(refreshToken).build();
    }

    private TokenParams accessTokenParams(User user) {
        return TokenParams.builder()
            .tokenType(TokenType.ACCESS_TOKEN)
            .issuer(tokenProperties.getIssuer())
            .audiences(tokenProperties.getAudiences())
            .userId(String.valueOf(user.getId()))
            .tokenTimeToLive(tokenProperties.getAccessTokenTimeToLive())
            .build();
    }

    private TokenParams refreshTokenParams(User user) {
        return TokenParams.builder()
            .tokenType(TokenType.REFRESH_TOKEN)
            .issuer(tokenProperties.getIssuer())
            .audiences(tokenProperties.getAudiences())
            .userId(String.valueOf(user.getId()))
            .tokenTimeToLive(tokenProperties.getRefreshTokenTimeToLive())
            .build();
    }
}
