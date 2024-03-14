package com.lechros.springtokenlogin.token;

import com.lechros.springtokenlogin.authentication.RegisteredOAuth2User;
import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${authorization.issuer}")
    private String issuer;
    @Value("${authorization.audience}")
    private String audience;
    @Value("${authorization.access-token-time-to-live}")
    private Long accessTokenTimeToLive;
    @Value("${authorization.refresh-token-time-to-live}")
    private Long refreshTokenTimeToLive;

    private final AccessTokenGenerator accessTokenGenerator;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final IssuedRefreshTokenRepository issuedRefreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccessTokenResponse issueNewAccessToken(RegisteredOAuth2User principal) {
        return generateToken(principal.getUser());
    }

    @Transactional
    public AccessTokenResponse refreshAccessToken(String refreshTokenValue, Long userId) {
        Optional<IssuedRefreshToken> maybeFoundRefreshToken = issuedRefreshTokenRepository.findByTokenValue(refreshTokenValue);
        if (maybeFoundRefreshToken.isEmpty()) {
            throw new RuntimeException("Invalid refresh token");
        }
        IssuedRefreshToken foundRefreshToken = maybeFoundRefreshToken.get();
        if (!foundRefreshToken.validate(refreshTokenValue)) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepository.findById(userId).orElseThrow();

        // Refresh token rotation 사용, 기존 토큰 무효화
        foundRefreshToken.invalidate();
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
            .issuer(issuer)
            .audience(audience)
            .userId(String.valueOf(user.getId()))
            .tokenTimeToLive(Duration.ofSeconds(accessTokenTimeToLive))
            .build();
    }

    private TokenParams refreshTokenParams(User user) {
        return TokenParams.builder()
            .tokenType(TokenType.REFRESH_TOKEN)
            .issuer(issuer)
            .audience(audience)
            .userId(String.valueOf(user.getId()))
            .tokenTimeToLive(Duration.ofSeconds(refreshTokenTimeToLive))
            .build();
    }
}
