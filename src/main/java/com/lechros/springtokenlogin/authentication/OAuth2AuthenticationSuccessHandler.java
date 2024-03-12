package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.data.AccessTokenResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final String REFRESH_TOKEN_EXPIRES_IN = OAuth2ParameterNames.REFRESH_TOKEN + "_" + OAuth2ParameterNames.EXPIRES_IN;

    @Value("${authorizationserver.redirect-uri}")
    private String redirectUri;

    private final OAuth2TokenService oauth2TokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AccessTokenResponse tokenResponse = oauth2TokenService.createTokenResponse(Long.valueOf(authentication.getName()));

        String uri = UriComponentsBuilder.fromHttpUrl(redirectUri)
            .queryParam(OAuth2ParameterNames.TOKEN_TYPE, tokenResponse.getTokenType())
            .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, tokenResponse.getAccessToken())
            .queryParam(OAuth2ParameterNames.EXPIRES_IN, tokenResponse.getExpiresIn())
            .queryParam(OAuth2ParameterNames.REFRESH_TOKEN, tokenResponse.getRefreshToken())
            .queryParam(REFRESH_TOKEN_EXPIRES_IN, tokenResponse.getRefreshTokenExpiresIn())
            .build(true).toString();

        response.sendRedirect(uri);
    }
}
