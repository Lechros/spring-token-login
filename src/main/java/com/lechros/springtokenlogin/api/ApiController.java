package com.lechros.springtokenlogin.api;

import com.lechros.springtokenlogin.user.User;
import com.lechros.springtokenlogin.user.UserRepository;
import com.lechros.springtokenlogin.user.UserSocialLogin;
import com.lechros.springtokenlogin.user.UserSocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;
    private final UserSocialLoginService userSocialLoginService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User vanished?"));
        UserSocialLogin userSocialLogin = userSocialLoginService.getUserSocialLogin(user);
        return String.format("You are user '%d', registered with '%s' login.", user.getId(), userSocialLogin.getProvider());
    }
}
