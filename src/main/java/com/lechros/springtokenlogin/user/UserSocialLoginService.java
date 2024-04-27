package com.lechros.springtokenlogin.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSocialLoginService {

    private final UserSocialLoginRepository userSocialLoginRepository;

    @Transactional(readOnly = true)
    public Optional<User> findUser(String provider, String providerId) {
        return userSocialLoginRepository.findByProviderAndProviderId(provider, providerId)
            .map(UserSocialLogin::getUser);
    }

    @Transactional
    public void addUserSocialLogin(User user, String provider, String providerId) {
        if (userSocialLoginRepository.findByProviderAndProviderId(provider, providerId).isPresent()) {
            throw new RuntimeException("User social login already exists");
        }

        UserSocialLogin userSocialLogin = UserSocialLogin.builder()
            .user(user)
            .provider(provider)
            .providerId(providerId)
            .build();
        userSocialLoginRepository.save(userSocialLogin);
    }
}
