package com.lechros.springtokenlogin.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("authorization")
@Getter
@RequiredArgsConstructor
public class AuthorizationProperties {
    /**
     * 소셜 로그인 처리 URI
     */
    private final String authorizationBaseUri;
    /**
     * 인증 요청 처리 URI
     */
    private final String redirectionBaseUri;

    private final String tokenUri;

    private final List<String> redirectUriParameterAllowedRegistrations;

    public String getAuthorizationUri() {
        return authorizationBaseUri + "/*";
    }

    public String getRedirectionUri() {
        return redirectionBaseUri + "/*";
    }
}
