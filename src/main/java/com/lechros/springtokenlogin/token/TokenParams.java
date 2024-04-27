package com.lechros.springtokenlogin.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class TokenParams {

    private TokenType tokenType;
    private String issuer;
    private List<String> audiences;
    private String userId;
    private Duration tokenTimeToLive;
}
