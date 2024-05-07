package com.lechros.springtokenlogin.config;

import com.lechros.springtokenlogin.authentication.*;
import com.lechros.springtokenlogin.provider.ClientSecretGenerator;
import com.lechros.springtokenlogin.provider.DynamicInMemoryClientRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AuthorizationConfig {

    private final AuthorizationProperties authorizationProperties;
    private final AuthorizedOAuth2UserService authorizedOAuth2UserService;
    private final AuthorizedOidcUserService authorizedOidcUserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final OAuth2ClientProperties clientProperties;
    private final List<ClientSecretGenerator> secretGenerators;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // SecurityFilterChain에서 소셜 로그인, 인증 요청 URI만 처리 -> 로그인 페이지 제거
            .securityMatcher(authorizationProperties.getAuthorizationUri(), authorizationProperties.getRedirectionUri())
            // 위 securityMatcher와 일치하도록 oauth2Login 설정 반영
            .oauth2Login(oauth2Login -> oauth2Login
                .authorizationEndpoint(authorization -> authorization
                    .baseUri(authorizationProperties.getAuthorizationBaseUri())
                    .authorizationRequestResolver(oauth2AuthorizationRequestResolver(
                        clientRegistrationRepository(clientProperties, secretGenerators)))
                    .authorizationRequestRepository(authorizationRequestRepository()))
                .redirectionEndpoint(redirection -> redirection
                    .baseUri(authorizationProperties.getRedirectionUri()))
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(authorizedOAuth2UserService)
                    .oidcUserService(authorizedOidcUserService))
                .successHandler(successHandler)
                .failureHandler(failureHandler))
            .cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        String authorizationBaseUri = authorizationProperties.getAuthorizationBaseUri();
        List<String> allowedRegistrationIds = authorizationProperties.getRedirectUriParameterAllowedRegistrations();
        return new RedirectUriParameterOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationBaseUri, allowedRegistrationIds);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new InMemoryOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties, List<ClientSecretGenerator> secretGenerators) {
        Map<String, ClientRegistration> registrations = new OAuth2ClientPropertiesMapper(properties).asClientRegistrations();
        return new DynamicInMemoryClientRegistrationRepository(registrations, secretGenerators);
    }
}
