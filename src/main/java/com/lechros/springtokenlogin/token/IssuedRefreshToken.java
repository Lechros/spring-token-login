package com.lechros.springtokenlogin.token;

import com.lechros.springtokenlogin.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class IssuedRefreshToken {

    @Id
    @GeneratedValue
    private Long id;

    private String tokenValue;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Long issuedAt;

    private Long expiresAt;

    private Boolean revoked;

    public void revoke() {
        if (revoked) {
            throw new RuntimeException("Already revoked");
        }
        revoked = true;
    }

    public boolean isExpired(Long now) {
        // 발급과 검증을 동일한 서버에서 진행하므로 clock skew를 고려할 필요가 없음
        return now < issuedAt || expiresAt < now;
    }

    public static IssuedRefreshToken from(AbstractOAuth2Token token, User user) {
        return IssuedRefreshToken.builder()
            .tokenValue(token.getTokenValue())
            .user(user)
            .issuedAt(token.getIssuedAt().getEpochSecond())
            .expiresAt(token.getExpiresAt().getEpochSecond())
            .revoked(false)
            .build();
    }
}
