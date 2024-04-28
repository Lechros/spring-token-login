package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface AuthorizedUserService {

    default AuthorizedUser getAuthorizedUser(OAuth2UserRequest oauth2userRequest, OAuth2User oauth2User, OAuth2Service oauth2Service) {
        String registrationId = oauth2userRequest.getClientRegistration().getRegistrationId();
        String oauth2UserName = oauth2User.getName();
        User user = oauth2Service.findOrCreateUser(registrationId, oauth2UserName);

        return new AuthorizedUser(oauth2User.getAuthorities(), oauth2User.getAttributes(), user);
    }
}
