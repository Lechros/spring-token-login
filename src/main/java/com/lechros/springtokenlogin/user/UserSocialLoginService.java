package com.lechros.springtokenlogin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSocialLoginService {

    private final UserRepository userRepository;
    private final UserSocialLoginRepository userSocialLoginRepository;

    @Transactional
    public User findOrRegister(String provider, String providerId) {
        return find(provider, providerId).orElseGet(() -> register(provider, providerId));
    }

    @Transactional(readOnly = true)
    public Optional<User> find(String provider, String providerId) {
        return userSocialLoginRepository.findByProviderAndProviderId(provider, providerId)
            .map(UserSocialLogin::getUser);
    }

    @Transactional
    public User register(String provider, String providerId) {
        // TODO: 적절한 기본 닉네임 설정
        User user = User.from("new user name");
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
