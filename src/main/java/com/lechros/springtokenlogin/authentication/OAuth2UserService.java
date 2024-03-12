package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.domain.SocialLogin;
import com.lechros.springtokenlogin.domain.User;
import com.lechros.springtokenlogin.repository.SocialLoginRepository;
import com.lechros.springtokenlogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final SocialLoginRepository socialLoginRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        User user = registerIfNew(oauth2User, userRequest);
        return oauth2User;
    }

    private User registerIfNew(OAuth2User oauth2User, OAuth2UserRequest userRequest) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getName();

        Optional<User> registeredUser = getRegisteredUser(provider, providerId);
        if (registeredUser.isPresent()) {
            return registeredUser.get();
        }

        User user = User.builder().nickname(oauth2User.getName()).build();
        user = userRepository.save(user);

        SocialLogin socialLogin = SocialLogin.builder()
            .userId(user.getId())
            .provider(provider)
            .providerId(providerId)
            .build();
        socialLogin = socialLoginRepository.save(socialLogin);

        return user;
    }

    private Optional<User> getRegisteredUser(String provider, String providerId) {
        return socialLoginRepository.findByProviderAndProviderUsername(provider, providerId)
            .flatMap(socialLogin -> userRepository.findById(socialLogin.getUserId()));
    }
}
