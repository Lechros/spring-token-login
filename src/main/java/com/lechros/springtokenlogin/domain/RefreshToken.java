package com.lechros.springtokenlogin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Setter
    private Long id;

    private String token;

    private Long userId;

    private Long issuedAt;

    private Long expiresAt;

    private Boolean invalidated;

    public boolean isValid(long numericDate) {
        // You may provide for some small leeway to account for clock skew.
        if (issuedAt != null && numericDate < issuedAt) {
            return false;
        }
        if (expiresAt != null && numericDate > expiresAt) {
            return false;
        }
        if (invalidated) {
            return false;
        }
        return true;
    }
}
