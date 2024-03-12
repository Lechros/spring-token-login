package com.lechros.springtokenlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthenticationConfig {

    /**
     * 소셜 로그인 처리 URI
     */
    private static String authorizationUri =
        OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
    /**
     * 인증 요청 처리 URI
     */
    private static String redirectionUri =
        OAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // SecurityFilterChain에서 소셜 로그인, 인증 요청 URI만 처리 -> 로그인 페이지 제거
            .securityMatcher(authorizationUri + "/*", redirectionUri)
            // 위 securityMatcher와 일치하도록 oauth2Login 설정 반영
            .oauth2Login(oauth2Login -> oauth2Login
                .authorizationEndpoint(authorization -> authorization
                    .baseUri(authorizationUri))
                .redirectionEndpoint(redirection -> redirection
                    .baseUri(redirectionUri)));

        return http.build();
    }
}
