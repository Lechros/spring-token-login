package com.lechros.springtokenlogin.token;

import com.lechros.springtokenlogin.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
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

    private Boolean invalidated;

    public boolean validate(String token, Long userId) {
        // TODO: 검증 실패 시 Exception 발생
        return tokenValue.equals(token) && user.getId().equals(userId) && checkTime(Instant.now()) && !getInvalidated();
    }

    public boolean checkTime(Instant now) {
        // 발급과 검증을 동일한 서버에서 진행하므로 clock skew를 고려할 필요가 없음
        Instant iat = Instant.ofEpochSecond(issuedAt);
        Instant exp = Instant.ofEpochSecond(expiresAt);
        return now.isAfter(iat) && now.isBefore(exp);
    }
}
