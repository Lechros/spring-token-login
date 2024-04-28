package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserRepository;
import com.lechros.springtokenlogin.user.UserSocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizedUserService {

    private final UserRepository userRepository;
    private final UserSocialLoginService userSocialLoginService;

    @Transactional
    public AuthorizedUser getAuthorizedUser(OAuth2UserRequest oauth2userRequest, OAuth2User oauth2User) {
        String registrationId = oauth2userRequest.getClientRegistration().getRegistrationId();
        String oauth2UserName = oauth2User.getName();

        Optional<User> foundUser = userSocialLoginService.findUser(registrationId, oauth2UserName);
        User user = foundUser.orElseGet(() -> {
            // 신규 유저 생성
            User newUser = User.withUsername(String.format("new %s user name", registrationId));

            newUser = userRepository.save(newUser);
            userSocialLoginService.addUserSocialLogin(newUser, registrationId, oauth2UserName);
            return newUser;
        });

        return new AuthorizedUser(oauth2User.getAuthorities(), oauth2User.getAttributes(), user);
    }
}
