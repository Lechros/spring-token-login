package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserRepository;
import com.lechros.springtokenlogin.user.UserSocialLogin;
import com.lechros.springtokenlogin.user.UserSocialLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisteredOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 토큰 정보 API에서 사용자 정보 받아오기
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        // 받아온 정보의 sub로 필요 시 DB에 저장하고 user 가져오기
        User user = findOrRegister(userRequest, oauth2User.getName());
        // name이 user.id(PK)로 설정된 OAuth2User 반환
        return RegisteredOAuth2User.from(oauth2User, user);
    }

    private User findOrRegister(OAuth2UserRequest userRequest, String providerId) {
        String provider = userRequest.getClientRegistration().getRegistrationId();

        return find(provider, providerId)
            .orElseGet(() ->
                // TODO: provider별로 oauth2User.getAttributes에서 username 가져오는 기능 추가
                register("new user", provider, providerId));
    }

    private Optional<User> find(String provider, String providerId) {
        return userSocialLoginRepository
            .findByProviderAndProviderId(provider, providerId)
            .map(UserSocialLogin::getUser);
    }

    private User register(String username, String provider, String providerId) {
        User user = User.from(username);
        user = userRepository.save(user);

        UserSocialLogin socialLogin = UserSocialLogin.builder()
            .user(user)
            .provider(provider)
            .providerId(providerId)
            .build();
        socialLogin = userSocialLoginRepository.save(socialLogin);

        return user;
    }
}
