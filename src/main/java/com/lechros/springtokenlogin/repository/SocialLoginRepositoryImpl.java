package com.lechros.springtokenlogin.repository;

import com.lechros.springtokenlogin.domain.SocialLogin;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SocialLoginRepositoryImpl implements SocialLoginRepository {

    private ConcurrentMap<String, ConcurrentMap<String, SocialLogin>> map = new ConcurrentHashMap<>();

    private long ai = 1;

    public Optional<SocialLogin> findByProviderAndProviderUsername(String provider, String providerUsername) {
        ConcurrentMap<String, SocialLogin> subMap = map.get(provider);
        if (subMap == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(subMap.get(providerUsername));
    }

    public SocialLogin save(SocialLogin socialLogin) {
        if (socialLogin.getId() == null) {
            socialLogin.setId(ai++);
        }
        ConcurrentMap<String, SocialLogin> subMap = map.computeIfAbsent(socialLogin.getProvider(), (provider) -> new ConcurrentHashMap<>());
        subMap.put(socialLogin.getProviderId(), socialLogin);
        return socialLogin;
    }
}
