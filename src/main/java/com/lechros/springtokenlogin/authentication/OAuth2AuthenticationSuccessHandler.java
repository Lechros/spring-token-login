package com.lechros.springtokenlogin.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lechros.springtokenlogin.token.AccessTokenResponse;
import com.lechros.springtokenlogin.token.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        RegisteredOAuth2User principal = (RegisteredOAuth2User) authentication.getPrincipal();
        AccessTokenResponse tokenResponse = tokenService.issueNewAccessToken(principal);

        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}
