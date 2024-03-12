package com.lechros.springtokenlogin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
public class SocialLogin {

    @Setter
    private Long id;

    private Long userId;

    private String provider;

    private String providerId;
}
