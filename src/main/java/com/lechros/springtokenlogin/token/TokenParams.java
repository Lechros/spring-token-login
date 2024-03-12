package com.lechros.springtokenlogin.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
@Builder
public class TokenParams {

    private TokenType tokenType;
    private String issuer;
    private String audience;
    private String userId;
    private Duration tokenTimeToLive;
}
