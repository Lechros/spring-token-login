package com.lechros.springtokenlogin.token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping(value = "${authorization.token-uri}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public AccessTokenResponse token(
        @RequestParam("grant_type") String grantType,
        @RequestParam("refresh_token") String refreshToken
    ) {
        System.out.println("TOKEN CONTROLLER ACCESSED");
        if (!OAuth2ParameterNames.REFRESH_TOKEN.equals(grantType)) {
            throw new RuntimeException("unsupported_grant_type");
        }

        return tokenService.refreshAccessToken(refreshToken);
    }
}
