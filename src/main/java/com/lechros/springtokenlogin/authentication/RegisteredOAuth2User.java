package com.lechros.springtokenlogin.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class RegisteredOAuth2User extends DefaultOAuth2User {

    private final String name;

    private RegisteredOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String name) {
        super(authorities, attributes, "");
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public static RegisteredOAuth2User from(OAuth2User oauth2User, String name) {
        return new RegisteredOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), name);
    }
}
