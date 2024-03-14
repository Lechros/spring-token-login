package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
@EqualsAndHashCode
@ToString
public class RegisteredOAuth2User implements OAuth2User {

    private final Set<GrantedAuthority> authorities;

    private final Map<String, Object> attributes;

    private final User user;

    private RegisteredOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, User user) {
        this.authorities = authorities != null ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities))) : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        this.user = user;
    }

    public String getName() {
        return user.getId().toString();
    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }

    public static RegisteredOAuth2User from(OAuth2User oauth2User, User user) {
        return new RegisteredOAuth2User(oauth2User.getAuthorities(), oauth2User.getAttributes(), user);
    }
}
