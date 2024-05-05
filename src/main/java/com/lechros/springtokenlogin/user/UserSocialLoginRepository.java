package com.lechros.springtokenlogin.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSocialLoginRepository extends JpaRepository<UserSocialLogin, Long> {

    Optional<UserSocialLogin> findByProviderAndProviderId(String provider, String providerId);

    Optional<UserSocialLogin> findByUser(User user);
}
