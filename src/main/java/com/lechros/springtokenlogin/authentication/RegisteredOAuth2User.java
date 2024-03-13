package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class RegisteredOAuth2User extends DefaultOAuth2User {

    private final String name;

    @Getter
    private final User user;

    private RegisteredOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, User user) {
        super(authorities, attributes, "");
        this.name = String.valueOf(user.getId());
        this.user = user;
    }

    @Override
    public String getName() {
        return name;
    }

    public static RegisteredOAuth2User from(OAuth2User oauth2User, User user) {
        return new RegisteredOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), user);
    }
}
