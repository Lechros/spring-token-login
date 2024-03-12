package com.lechros.springtokenlogin.repository;

import com.lechros.springtokenlogin.domain.SocialLogin;

import java.util.Optional;

public interface SocialLoginRepository {

    Optional<SocialLogin> findByProviderAndProviderUsername(String provider, String providerUsername);

    SocialLogin save(SocialLogin socialLogin);
}
