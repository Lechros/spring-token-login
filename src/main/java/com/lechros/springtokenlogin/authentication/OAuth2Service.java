package com.lechros.springtokenlogin.authentication;

import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserRepository;
import com.lechros.springtokenlogin.user.UserSocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final UserSocialLoginService userSocialLoginService;

    @Transactional
    public User findOrCreateUser(String provider, String providerId) {
        Optional<User> optionalUser = userSocialLoginService.findUser(provider, providerId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = User.withUsername(String.format("new %s user name", provider));
        user = userRepository.save(user);
        userSocialLoginService.addUserSocialLogin(user, provider, providerId);

        return user;
    }
}
