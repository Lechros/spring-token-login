package com.lechros.springtokenlogin.repository;

import com.lechros.springtokenlogin.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private ConcurrentMap<String, RefreshToken> map = new ConcurrentHashMap<>();

    private long ai = 1;

    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(map.get(token));
    }

    public RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken.getId() == null) {
            refreshToken.setId(ai++);
        }
        map.put(refreshToken.getToken(), refreshToken);
        return refreshToken;
    }

}
