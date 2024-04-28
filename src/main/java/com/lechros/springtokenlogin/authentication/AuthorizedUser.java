package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
@EqualsAndHashCode
@ToString
public class AuthorizedUser implements OAuth2User, OidcUser {

    private final Set<GrantedAuthority> authorities;

    private final Map<String, Object> attributes;

    private final User user;

    public AuthorizedUser(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, User user) {
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

    @Override
    public Map<String, Object> getClaims() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OidcIdToken getIdToken() {
        throw new UnsupportedOperationException();
    }
}
