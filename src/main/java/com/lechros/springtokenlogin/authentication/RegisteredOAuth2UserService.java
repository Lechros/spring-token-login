package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserSocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisteredOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserSocialLoginService userSocialLoginService;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 토큰 정보 API에서 사용자 정보 받아오기
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        // 받아온 정보의 sub로 필요 시 DB에 저장하고 user 가져오기
        User user = userSocialLoginService.findOrRegister(registrationId, oauth2User.getName());
        // name이 user.id(PK)로 설정된 OAuth2User 반환
        return RegisteredOAuth2User.from(oauth2User, user);
    }
}
