package com.lechros.springtokenlogin.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Set;

@Getter
@AllArgsConstructor
@Builder
public class TokenContext {

    private TokenType tokenType;

    private String issuer;

    private String audience;

    private Duration tokenTimeToLive;

    private Long userId;

    private Set<String> authorizedScopes;
}
