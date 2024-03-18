package com.lechros.springtokenlogin.token;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;

public interface TokenGenerator<T extends AbstractOAuth2Token> {
    T generate(TokenParams params);
}
